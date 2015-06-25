package submission;

import static util.Util.parseServerNumber;
import static util.Util.prepareData;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

import domain.Expression;
import util.FailureContext;
import util.SizeComparator;
import util.TaskFilter;
import util.TestDataReader;
import util.Util;
import domain.Operator;
import frontend.EvalRequest;
import frontend.EvalResponse;
import frontend.Frontend;
import frontend.Guess;
import frontend.GuessResponse;
import frontend.Problem;
import frontend.ProblemDescriptor;
import frontend.TrainRequest;
import frontend.TrainingProblem;
import static frontend.Frontend.FrontendTask;

/**
 * Try our magnificent thingie
 */
public class Submission {

    private static Frontend frontend = new Frontend();

    private final SolverPlugin plugin;
    private final boolean writeReports;
    private final boolean writeTask;

    // data describing current task
    private ProblemDescriptor problem;
    private String[] inputs;
    private String[] results;

    private class SolutionContext implements GuessVerifier {
        private Expression lastSolution;

        @Override
        public Map<BigInteger, BigInteger> findFailedData(final Expression ex) throws SubmissionFailedException, SubmissionSucceededException {
            System.out.println("Kostia: Verifying solution " + ex + " on server.");
            GuessResponse guessResp = frontend.retryTask(new FrontendTask<GuessResponse>() {
                @Override
                public GuessResponse doTask(Frontend frontend) {
                    return frontend.guess(Guess.programGuess(problem.getId(), "(lambda (x) " + ex.toString() + ")"));
                }
            });
            switch (guessResp.getStatus()) {
                case win:
                    // this exception should go all way back to search caller
                    lastSolution = ex;
                    throw new SubmissionSucceededException("Successfully submitted " + ex);
                case error:
                    System.err.println("Kostia: Submission error " + guessResp.getMessage() + " for problemId id " + problem.getId());
                    throw new SubmissionFailedException(guessResp.getMessage(), problem.getId());
                case mismatch:
                    // add data to constraint set capture
                    Map<BigInteger, BigInteger> result = new HashMap<>();
                    result.put(parseServerNumber(guessResp.failedInput()), parseServerNumber(guessResp.correctOutput()));
                    System.out.println("New data received from server: "+result);
                    return result;
            }
            throw new RuntimeException("Unknown status: " + guessResp.getStatus());
        }

        private Expression getLastSolution() {
            return lastSolution;
        }
    }

    public Submission(SolverPlugin plugin) {
        this(plugin, true, true);
    }

    public Submission(SolverPlugin plugin, boolean writeTask, boolean writeReports) {
        this.plugin = plugin;
        this.writeTask = writeTask;
        this.writeReports = writeReports;
    }

    public void setup(final int complexity, final String... ops) {
        problem = frontend.retryTask(new FrontendTask<ProblemDescriptor>() {
            @Override
            public ProblemDescriptor doTask(Frontend frontend) {
                return frontend.train(TrainRequest.request(complexity, ops));
            }
        });
    }

    public void setup(String id) {
        problem = Util.getProblemById(id);
    }

    public void setupManually(String id, int size, String... ops) {
        TrainingProblem tp = new TrainingProblem();
        tp.setId(id);
        tp.setOperators(ops);
        tp.setSize(size);
        problem = tp;
    }

    public void setup(Problem problem) {
        this.problem = problem;
    }

    public boolean submit() {
        System.out.println("Kostia: Fetching initial data for problem " + problem);
        long startTime = System.currentTimeMillis();
        inputs = Util.getTestDataPack();
        EvalResponse response = frontend.retryTask(new FrontendTask<EvalResponse>() {
            @Override
            public EvalResponse doTask(Frontend frontend) {
                return frontend.evaluate(EvalRequest.programRequestById(problem.getId(), inputs));
            }
        });
        results = response.getOutputs();
        dumpProblemToFile();
        try {
            SolutionContext context = new SolutionContext();
            plugin.doSearch(
                    context,
                    problem.getId(),
                    prepareData(inputs),
                    prepareData(results),
                    problem.getSize(),
                    Operator.fromStrings(problem.getOperators()));
            long timeTaken = System.currentTimeMillis() - startTime;
            System.out.println(problem.getId() + " solved in " + (timeTaken/1000) + " sec");
            reportSuccess(context.getLastSolution().toString());
            return true;
        } catch (SolutionNotFoundExcepition ex) {
            System.out.println("Kostia: We didn't find any solutions.");
            recordProblem();
        } catch (RuntimeException rex) {
            System.out.println("Kostia: Submission was rejected for technical reasons.");
            rex.printStackTrace();
            recordProblem();
        }
        return false;
    }

    private void dumpProblemToFile() {
        if (writeTask) {
            FailureContext context = problem instanceof Problem
                    ? FailureContext.realProblem("None", (Problem) problem, inputs, results)
                    : FailureContext.trainingProblem("None", (TrainingProblem) problem, inputs, results);
            TestDataReader.writeFile("dump-" + problem.getId(), context);
        }
    }

    private void reportSuccess(String solution) {
        if (writeReports) {
            try {
                FileOutputStream fos = new FileOutputStream(problem.getId()+"-solved");
                fos.write(solution.getBytes(Charset.forName("US-ASCII")));
                fos.close();
            } catch (Exception ex) {}
        }
    }

    private void recordProblem() {
        if (writeReports) {
            try {
                new FileOutputStream(problem.getId()+"-failed").close();
            } catch (Exception ex) {}
        }
    }

    private static Collection<Problem> getRemainingProblems() {
        System.out.println("Kostia: Fetching remaining tasks.");
        TaskFilter filter = new TaskFilter(frontend.getProblems());
        filter.removeSolved();
        filter.excludeGreaterThan(11);
//        filter.excludeOperator(Operator.fold.name());
//        filter.excludeOperator(Operator.tfold.name());
//        filter.excludeOperator(Operator.if0.name());
        filter.sort(new SizeComparator());
        return filter.getProblems();
    }

    private static void doTestTasks(Submission sub, int size, String ... ops) {
        int success = 0;
        int failure = 0;
        int total = 0;
        for(;;) {
            System.out.println("Kostia: Trying task " + total + " [succeeded " + success + ", failed " + failure + "]");
            sub.setup(size, ops);
            if (sub.submit())
                success++;
            else
                failure++;
            total++;
        }
    }

    private static void doSubmissionTask(Submission sub, int maxTasks) {
        Collection<Problem> problems = getRemainingProblems();
        int of = Math.min(problems.size(), maxTasks);
        int n = 1;
        for (Iterator<Problem> iterator = problems.iterator(); iterator.hasNext() && n<=maxTasks; n++) {
            Problem prob = iterator.next();
            System.out.println("Kostia: Preparing problem " + n + "/" + of);
            sub.setup(prob);
            if (!sub.submit()) break;
        }
    }

    public static void main(String[] args) {


        if (args.length==0) {
            Submission sub = new Submission(new SolverPluginKostia("PESSIMISTIC"));

//        doSubmissionTask(sub, Integer.MAX_VALUE);

//        sub.setup("grS3aISwyRkbKeDVmacUvpAz"); // failing with fold
//        sub.submit();

            doTestTasks(sub, 13);
        } else {
            String id = args[0];
            String mode = args.length>1?args[1]:"PESSIMISTIC";
            boolean writeTask = args.length>2 && "true".equalsIgnoreCase(args[2]);
            Submission sub = new Submission(new SolverPluginKostia(mode), writeTask, false);
            sub.setup(id);
            if (sub.submit()) {
                System.exit(0);
            } else {
                System.exit(2);
            }
        }
    }
}
