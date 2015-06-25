package domain;

import frontend.*;
import frontend.exceptions.RetryLaterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import tests.ServerTest;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static util.Util.*;

@Category(ServerTest.class)
public class ServerComparatorTest {

    private Frontend frontend = new Frontend();

    private int failureCount;

    @Before
    public void resetFailureCount() {
        failureCount = 0;
    }

    @Test
    public void testCompareWithServer() {
        TrainingProblem problem = fetchTestTask();
        compareWithServer(problem.getChallenge());
    }

    @Test
    public void testCompareWithServerId() {
        compareWithServer("(lambda (x_21216) x_21216)");
    }

    @Test
    public void testCompareWithServerNot() {
        compareWithServer("(lambda (x_21216) (not x_21216))");
    }

    @Test
    public void testCompareWithServerShl1() {
        compareWithServer("(lambda (x_21216) (shl1 x_21216))");
    }

    @Test
    public void testCompareWithServerShr1() {
        compareWithServer("(lambda (x_21216) (shr1 x_21216))");
    }

    @Test
    public void testCompareWithServerShr4() {
        compareWithServer("(lambda (x_21216) (shr4 x_21216))");
    }

    @Test
    public void testCompareWithServerShr16() {
        compareWithServer("(lambda (x_21216) (shr16 x_21216))");
    }

    @Test
    public void testCompareWithServerAnd() {
        compareWithServer("(lambda (x_21216) (and x_21216 1))");
    }

    @Test
    public void testCompareWithServerOr() {
        compareWithServer("(lambda (x_21216) (or x_21216 1))");
    }

    @Test
    public void testCompareWithServerXor() {
        compareWithServer("(lambda (x_21216) (xor x_21216 1))");
    }

    @Test
    public void testCompareWithServerPlus() {
        compareWithServer("(lambda (x_21216) (plus x_21216 1))");
    }

    @Test
    public void testCompareWithServerFold() {
        compareWithServer("(lambda (input) (fold (not (shr4 input)) 1 (lambda (x acc) (or x (shr4 acc)))))");
    }

    private void compareWithServer(String sourceCode) {
        String[] testData = getTestDataPack();
        Program program = new Program(sourceCode);
        EvalResponse proper = evaluate(EvalRequest.programRequest(sourceCode, testData));
        for(int i=0;i<testData.length;i++) {
            String input = testData[i];
            String correctVal = proper.getOutputs()[i];
            assertValue(program, input, correctVal);
        }
        if (failureCount>0) {
            System.out.println("Mismatch found for program: " + sourceCode);
        }
        assertThat("Failure count", failureCount, is(0));
    }

    private static final int DATA_PACK_SIZE = 256;

    private String[] getTestDataPack() {
        String[] testData = new String[DATA_PACK_SIZE];
        int index = 0;
        String[] walk = getWalkingBitData();
        System.arraycopy(walk, 0, testData, index, walk.length);
        index += walk.length;
        String[] ladder = getLadderData();
        System.arraycopy(ladder, 0, testData, index, ladder.length);
        index += ladder.length;
        String[] revLadder = getReverseLadderData();
        System.arraycopy(revLadder, 0, testData, index, revLadder.length);
        index += revLadder.length;
        String[] randomData = getRandomTestData(DATA_PACK_SIZE-index);
        System.arraycopy(randomData, 0, testData, index, randomData.length);
        return testData;
    }

    private void assertValue(Program program, String input, String expected) {
        BigInteger inputNumber = new BigInteger(input, 16);
        BigInteger result = program.evaluate(inputNumber);
        BigInteger correct = parseServerNumber(expected);
        if (!correct.equals(result)) {
            failureCount++;
            System.out.println("Failure: input " + inputNumber.toString(16) + ", output " + result.toString(16) + ", correct " + correct.toString(16));
        }
    }

    private TrainingProblem fetchTestTask() {
        do {
            try {
                return frontend.train(TrainRequest.request(11, "fold"));
            } catch (RetryLaterException e) {
                try {
                    System.out.println("Throttling fetch test task...");
                    Thread.sleep(10000); // wait 10 sec
                } catch (InterruptedException e1) {
                    // bla-bla
                }
            }
        } while (true);
    }

    private EvalResponse evaluate(EvalRequest request) {
        do {
            try {
                return frontend.evaluate(request);
            } catch (RetryLaterException e) {
                try {
                    System.out.println("Throttling evaluate...");
                    Thread.sleep(10000); // wait 10 sec
                } catch (InterruptedException e1) {
                    // bla-bla
                }
            }
        } while (true);
    }
}
