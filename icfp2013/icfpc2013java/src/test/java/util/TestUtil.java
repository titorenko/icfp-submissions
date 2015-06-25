package util;

import frontend.Problem;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class TestUtil {

    @Test
    public void testReadProblemFromResource() {
        Problem problem = Util.getProblemById("02GJ1A32WAXH3JbazT1FrJu6");
        assertThat(problem.getSize(), is(9));
        assertThat(problem.getOperators(), is(new String[]{"and", "if0"}));

        Problem problem2 = Util.getProblemById("zzh5sCHfYSajCU9J9T5kfn7y");
        assertThat(problem2.getSize(), is(27));
        assertThat(problem2.getOperators(), is(new String[]{"and", "if0", "or", "plus", "shr4", "tfold", "xor"}));
    }
}
