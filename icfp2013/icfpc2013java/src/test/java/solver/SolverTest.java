package solver;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import domain.Operator;
import domain.Program;
import frontend.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Ignore;
import org.junit.Test;
import search.ProblemDefinitionParser;
import search.TrainProblemDumper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SolverTest {
    @Test
    @Ignore
    public void testSolveProblem5() {
        Set<Operator> operators = Sets.newHashSet(Operator.id0, Operator.shr4, Operator.shr1);
        List<BigInteger> xs = Lists.newArrayList(
                new BigInteger("65535", 16),
                new BigInteger("43690", 16),
                new BigInteger("4294967295", 16)
        );
        List<BigInteger> ys = Lists.newArrayList(
                new BigInteger("32A", 16),
                new BigInteger("21B", 16),
                new BigInteger("214A4B39", 16)
        );
        Solver solver = new Solver(operators, 10, xs, ys);
        assertEquals("(lambda (a0) (shr4 (shr1 (shr4 a0))))", solver.solve().toString());
    }

    @Test
    @Ignore
    public void testSolveProblem2() {
        // "and","if0","shl1","shr16","shr4","xor"
        Set<Operator> operators = Sets.newHashSet(
                Operator.id0,
                Operator.and,
                Operator.if0,
                Operator.shl1,
                Operator.shr16,
                Operator.shr4,
                Operator.xor
        );
        Program program = new Program("(lambda (x_29275) "
                + "(shr16 (shr4 (and (xor (shl1 (and 0 (if0 (shr16 (xor (and (xor x_29275 x_29275) 0) x_29275)) x_29275 0))) x_29275) x_29275))))");

        List<BigInteger> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            BigInteger x = new BigInteger(64, random);
            xs.add(x);
            ys.add(program.evaluate(x));
        }
        Solver solver = new Solver(operators, 10, xs, ys);
        assertEquals("(lambda (a0) (shr4 (shr16 a0)))", solver.solve().toString());
    }

    @Test
    @Ignore
    public void fileTrainTest() throws URISyntaxException, IOException {
        List<ProblemDef> problemDefs = loadTrainProblems();
        int total = 0, fail = 0;
        Set<String> unsolved = new HashSet<>();
        for (ProblemDef problem : problemDefs) {
            // if (!problem.operators.contains(Operator.fold) && !problem.operators.contains(Operator.tfold)) {
            System.out.println(problem.id + " " + problem.solution);
            Solver solver = new Solver(problem.operators, problem.size, problem.xs, problem.ys);
            Prog prog = solver.solve();
            System.out.println(prog);
            total++;
            if (prog == null) {
                fail++;
                unsolved.add(problem.id);
            }
            // }
        }
        System.out.println("total: " + total + " failed: " + fail + " not solved: " + unsolved);
    }

    @Test
    @Ignore
    public void fileTrainBonusTest() throws URISyntaxException, IOException {
        List<ProblemDef> problemDefs = loadTrainProblems();
        int total = 0, fail = 0;
        Set<String> unsolved = new HashSet<>();
        for (ProblemDef problem : problemDefs) {
            if (problem.operators.contains(Operator.bonus)) {
                System.out.println(problem.id + " " + problem.solution);
                BonusSolver solver = new BonusSolver(problem.operators, problem.size, problem.xs, problem.ys);
                Prog prog = solver.solve();
                System.out.println(prog);
                total++;
                if (prog == null) {
                    fail++;
                    unsolved.add(problem.id);
                }
            }
        }
        System.out.println("total: " + total + " failed: " + fail + " not solved: " + unsolved);
    }

    @Test
    @Ignore
    public void serverTrainTest() {
        Frontend frontend = new Frontend();
        TrainingProblem problem = frontend.train(TrainRequest.request(42));
        System.out.println(problem);
        List<BigInteger> xs = getRandomTestData(23);
        EvalRequest evalRequest = EvalRequest.programRequest(problem.getChallenge(), xs);
        System.out.println(evalRequest);
        EvalResponse response = frontend.evaluate(evalRequest);
        System.out.println(response);
        List<BigInteger> ys = response.getNumericOutputs();
        // solve
        boolean done = false;
        while (!done) {
            BonusSolver solver = new BonusSolver(problem.getOperatorsSet(), problem.getSize(), xs, ys);
            Prog program = solver.solve();
            Guess guessRequest = Guess.programGuess(problem.getId(), program.toString());
            System.out.println(guessRequest);
            GuessResponse guessResponse = frontend.guess(guessRequest);
            System.out.println(guessResponse);
            done = guessResponse.getStatus() != GuessResponseStatus.mismatch;
            if (!done) {
                xs.add(new BigInteger(guessResponse.getValues()[0].substring(2), 16));
                ys.add(new BigInteger(guessResponse.getValues()[1].substring(2), 16));
            }
        }
    }

    @Test
    @Ignore
    public void serverRealTest() {
        Frontend frontend = new Frontend();
        Problem[] problems = frontend.getProblems();
        Problem problem = null;
        String id = "";
        for (int i = 0; i < problems.length; i++) {
            if (id.equals(problems[i].getId())) {
                problem = problems[i];
                break;
            }
        }
        if (problem == null) {
            System.out.println("cant find problem");
        }
        System.out.println(problem);
        List<BigInteger> xs = getRandomTestData(256);
        EvalRequest evalRequest = EvalRequest.programRequestById(problem.getId(), xs);
        System.out.println(evalRequest);
        EvalResponse response = frontend.evaluate(evalRequest);
        System.out.println(response);
        List<BigInteger> ys = response.getNumericOutputs();
        // solve
        boolean done = false;
        while (!done) {
            Solver solver = new Solver(problem.getOperatorsSet(), problem.getSize(), xs, ys);
            Prog program = solver.solve();
            Guess guessRequest = Guess.programGuess(problem.getId(), program.toString());
            System.out.println(guessRequest);
            GuessResponse guessResponse = frontend.guess(guessRequest);
            System.out.println(guessResponse);
            done = guessResponse.getStatus() != GuessResponseStatus.mismatch;
            if (!done) {
                xs.add(new BigInteger(guessResponse.getValues()[0].substring(2), 16));
                ys.add(new BigInteger(guessResponse.getValues()[1].substring(2), 16));
            }
        }
    }

    public List<BigInteger> getRandomTestData(int size) {
        Random random = new Random();
        List<BigInteger> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new BigInteger(64, random));
        }
        return result;
    }

    public List<ProblemDef> loadTrainProblems() throws URISyntaxException, IOException {
        File root = new File(TrainProblemDumper.class.getResource("/").toURI());
        File[] problems = root.listFiles((FileFilter) new WildcardFileFilter("*.txt"));
        List<ProblemDef> result = new ArrayList<>();
        for (int i = 0; i < problems.length; i++) {
            Properties properties = new Properties();
            properties.load(ProblemDefinitionParser.class.getResourceAsStream("/" + problems[i].getName()));
            // parse problem
            String solution = properties.getProperty("solution");
            String id = problems[i].getName();
            final String opsString = properties.getProperty("ops");
            Set<Operator> operators = Sets.newHashSet(
                    Collections2.transform(Arrays.asList(opsString.substring(1, opsString.length() - 1).split(", ")),
                            new Function<String, Operator>() {
                                @Override
                                public Operator apply(String input) {
                                    return Operator.valueOf(input.trim());
                                }
                            }));
            int size = Integer.parseInt(properties.getProperty("size"));
            String xsString = properties.getProperty("x");
            List<BigInteger> xs = Lists.transform(Arrays.asList(xsString.substring(1, xsString.length() - 1).split(", ")),
                    new Function<String, BigInteger>() {
                        @Override
                        public BigInteger apply(String input) {
                            input = input.trim();
                            if (input.startsWith("0x")) {
                                input = input.substring(2);
                            }
                            return new BigInteger(input, 16);
                        }
                    });
            String ysString = properties.getProperty("y");
            List<BigInteger> ys = Lists.transform(Arrays.asList(ysString.substring(1, ysString.length() - 1).split(", ")),
                    new Function<String, BigInteger>() {
                        @Override
                        public BigInteger apply(String input) {
                            input = input.trim();
                            if (input.startsWith("0x")) {
                                input = input.substring(2);
                            }
                            return new BigInteger(input, 16);
                        }
                    });

            result.add(new ProblemDef(solution, id, operators, size, xs, ys));
        }
        return result;
    }

    private static class ProblemDef {
        final public String solution;
        final public String id;
        final public Set<Operator> operators;
        final public int size;
        final public List<BigInteger> xs;
        final public List<BigInteger> ys;

        private ProblemDef(String solution, String id, Set<Operator> operators, int size, List<BigInteger> xs, List<BigInteger> ys) {
            this.solution = solution;
            this.id = id;
            this.operators = operators;
            this.size = size;
            this.xs = xs;
            this.ys = ys;
        }

        public String toString() {
            return id;
        }
    }
}
