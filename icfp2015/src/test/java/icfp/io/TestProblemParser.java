package icfp.io;

import icfp.io.model.Problem;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestProblemParser {
    @Test
    public void loadAllProblems() {
        Parser parser = new Parser();
        for(int i=0;i<24;i++) {
            String name = "/problems/problem_" + i + ".json";
            try {
                Problem p = parser.parse(name);
                assertThat(p, notNullValue());
            } catch (Exception e) {
                fail("Filed to load problem " + name);
            }
        }
    }
}
