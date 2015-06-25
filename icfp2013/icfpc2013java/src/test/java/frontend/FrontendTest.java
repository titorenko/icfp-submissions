package frontend;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import tests.ServerTest;
import static org.junit.Assert.fail;
import static util.Util.getRandomNumber;

public class FrontendTest {

    private Frontend frontend = new Frontend();
    @Category(ServerTest.class)
    @Test
    public void testEvalTestProblem() {
        TrainingProblem problem = frontend.train(TrainRequest.request(5));

        EvalResponse response = frontend.evaluate(EvalRequest.programRequest(problem.getChallenge(), getRandomTestData(5)));

        System.out.println(response);
    }

    @Category(ServerTest.class)
    @Test
    public void testBadRequest() {
        try {
            frontend.evaluate(EvalRequest.programRequestById("0B1LHXHWB99qmtAcHN0TYglZ", getRandomTestData(1)));
            fail("This is a request for non-existent id.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testRandomData() {
        System.out.println(getRandomNumber());
        System.out.println(getRandomNumber());
        System.out.println(getRandomNumber());
        System.out.println(getRandomNumber());
    }

    @Test
    public void getTeamStatus() {
        System.out.println(frontend.status());
    }

    private String[] getRandomTestData(int size) {
        String[] data = new String[size];
        for(int i=0;i<size;i++) {
            data[i] = getRandomNumber();
        }
        return data;
    }
}
