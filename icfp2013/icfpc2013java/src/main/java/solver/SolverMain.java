package solver;

import frontend.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static frontend.Frontend.FrontendTask;

public class SolverMain {

    private static Frontend frontend = new Frontend();

    public static void main(String[] args) throws Exception {
    	Thread.sleep(30000);
        String taskId = args[0];
        ProblemDescriptor problem;
        if (taskId.equalsIgnoreCase("test")) {
            problem = getTestProblem();
        } else {
            problem = getProblemById(getProblems(), taskId);
        }
        System.out.println("Sergey: solving " + problem.getId());
        List<BigInteger> xs = getRandomTestData(256);
        EvalResponse response = getEvalResponse(EvalRequest.programRequestById(problem.getId(), xs));
        List<BigInteger> ys = response.getNumericOutputs();
        // solve
        while (true) {
            Solver solver = new Solver(problem.getOperatorsSet(), problem.getSize(), xs, ys);
            Prog program = solver.solve();
            if (program==null) {
                System.out.println("Sergey: timed-out");
                System.exit(2);
            }
            System.out.println("Sergey: guessing " + program.toString());
            Guess guessRequest = Guess.programGuess(problem.getId(), program.toString());
            GuessResponse guessResponse = getGuessResponse(guessRequest);
            switch (guessResponse.getStatus()) {
                case win:
                    System.out.println("Sergey: found solution");
                    System.exit(0);
                case mismatch:
                    System.out.println("Sergey: adding test data from mismatch");
                    xs.add(new BigInteger(guessResponse.getValues()[0].substring(2), 16));
                    ys.add(new BigInteger(guessResponse.getValues()[1].substring(2), 16));
                    break;
                case error:
                    System.out.println("Sergey: solution guess failure");
                    System.exit(3);
            }
        }
    }

    private static GuessResponse getGuessResponse(final Guess guessRequest) {
        return frontend.retryTask(new FrontendTask<GuessResponse>() {
            @Override
            public GuessResponse doTask(Frontend frontend) {
                return frontend.guess(guessRequest);
            }
        });
    }

    private static EvalResponse getEvalResponse(final EvalRequest evalRequest) {
        return frontend.retryTask(new FrontendTask<EvalResponse>() {
            @Override
            public EvalResponse doTask(Frontend frontend) {
                return frontend.evaluate(evalRequest);
            }
        });
    }

    private static ProblemDescriptor getProblemById(Problem[] problems, String id) {
        Problem problem = null;
        for (Problem problem1 : problems) {
            if (id.equals(problem1.getId())) {
                problem = problem1;
                break;
            }
        }
        if (problem == null) {
            System.out.println("Sergey: can't find problem");
            System.exit(1);
        }
        return problem;
    }

    private static ProblemDescriptor getTestProblem() {
        return frontend.retryTask(new FrontendTask<TrainingProblem>() {
            @Override
            public TrainingProblem doTask(Frontend frontend) {
                return frontend.train(TrainRequest.request(10));
            }
        });
    }

    public static List<BigInteger> getRandomTestData(int size) {
        Random random = new Random();
        List<BigInteger> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new BigInteger(64, random));
        }
        return result;
    }

    private static Problem[] getProblems() {
        return frontend.retryTask(new FrontendTask<Problem[]>() {
            @Override
            public Problem[] doTask(Frontend frontend) {
                return frontend.getProblems();
            }
        });
    }

}
