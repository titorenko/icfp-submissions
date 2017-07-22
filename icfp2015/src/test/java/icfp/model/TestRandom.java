package icfp.model;

import model.Random;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestRandom {

    public static final int SEED = 17;
    private static int sample[] = {0,24107,16552,12125,9427,13152,21440,3383,6873,16117};

    @Test
    public void compareExampleSequenceStatics() {
        long seed = SEED;
        int generated[] = new int[sample.length];
        for(int i=0;i<generated.length;i++) {
            generated[i] = Random.valueOf(seed);
            seed = Random.nextSeed(seed);
        }
        assertThat(generated, is(sample));
    }

    @Test
    public void compareExampleSequenceObjects() {
        Random rnd = new Random(SEED);
        int generated[] = new int[sample.length];
        for(int i=0;i<generated.length;i++) {
            generated[i] = rnd.next();
        }
        assertThat(generated, is(sample));
    }
}
