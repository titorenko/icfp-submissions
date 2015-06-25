package solver;

import frontend.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SolverBonusAuto {

    private static Frontend frontend = new Frontend();

    public static void main(String[] args) throws Exception {
        //Thread.sleep(60000);
        int value = 0;
        int modulo = Integer.MAX_VALUE;
        try {
            value = Integer.parseInt(args[0]);
            modulo = Integer.parseInt(args[1]);
        } catch (Exception ex) {}
        Problem[] problems = getProblems();
        for (Problem problem : problems) {
            if (modulo!=Integer.MAX_VALUE && (problem.getId().hashCode()%modulo!=value)) {
                continue;
            }
            if (!problem.getOperators()[0].equals("bonus")) {
                continue;
            }
            if (problem.getSolved() != null) {
                continue;
            }
            if (problem.getSize() > 21) {
                continue;
            }
            if (problem.getOperators().length > 6) {
                //continue;
            }
            System.out.println("Sergey Bonus: solving " + problem);
            List<BigInteger> xs = getRandomTestData(23);
            EvalRequest evalRequest = EvalRequest.programRequestById(problem.getId(), xs);
            System.out.println(evalRequest);
            EvalResponse response = getEvalResponse(evalRequest);
            System.out.println(response);
            List<BigInteger> ys = response.getNumericOutputs();
            // solve
            boolean done = false;
            while (!done) {
                BonusSolver solver = new BonusSolver(problem.getOperatorsSet(), problem.getSize(), xs, ys);
                Prog program = solver.solve();
                if (program == null) {
                    System.out.println("Sergey Bonus: timed-out");
                    done = true;
                    continue;
                    //System.exit(2);
                }
                System.out.println("Sergey Bonus: guessing " + program.toString());
                Guess guessRequest = Guess.programGuess(problem.getId(), program.toString());
                GuessResponse guessResponse = getGuessResponse(guessRequest);
                System.out.println("Sergey Bonus:  " + guessResponse);
                if (guessResponse.getStatus() == GuessResponseStatus.win) {
                    System.out.println("Sergey Bonus: found solution");
                    done = true;
                    continue;
                    //System.exit(0);
                }
                done = guessResponse.getStatus() != GuessResponseStatus.mismatch;
                if (!done) {
                    xs.add(new BigInteger(guessResponse.getValues()[0].substring(2), 16));
                    ys.add(new BigInteger(guessResponse.getValues()[1].substring(2), 16));
                }
            }
        }
        System.out.println("Sergey Bonus: solution guess failure");
        System.exit(3);
    }

    private static GuessResponse getGuessResponse(final Guess guessRequest) {
        return frontend.retryTask(new Frontend.FrontendTask<GuessResponse>() {
            @Override
            public GuessResponse doTask(Frontend frontend) {
                return frontend.guess(guessRequest);
            }
        });
    }

    private static EvalResponse getEvalResponse(final EvalRequest evalRequest) {
        return frontend.retryTask(new Frontend.FrontendTask<EvalResponse>() {
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
            System.out.println("Sergey Bonus: can't find problem");
            System.exit(1);
        }
        return problem;
    }

    private static ProblemDescriptor getTestProblem() {
        return frontend.retryTask(new Frontend.FrontendTask<TrainingProblem>() {
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
        return frontend.retryTask(new Frontend.FrontendTask<Problem[]>() {
            @Override
            public Problem[] doTask(Frontend frontend) {
                return frontend.getProblems();
            }
        });
    }
}
