package model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class TestScore {
    @Test
    public void testPoints() {
        Score score = Score.initial();
        Score newScore = score.addMove(13, 0);
        assertThat(newScore.getScore(), is(13));
    }

    @Test
    public void testSingleLineBonus() {
        Score score = Score.initial();
        Score newScore = score.addMove(13, 1);
        assertThat(newScore.getScore(), is(113));
    }

    @Test
    public void testMultiLineBonus() {
        Score score = Score.initial();
        assertThat(score.addMove(13, 2).getScore(), is(313));
        assertThat(score.addMove(13, 3).getScore(), is(613));
    }

    @Test
    public void previousLineBonus() {
        assertThat(new Score(0, 1, 0).addMove(10, 0).getScore(), is(10));
        assertThat(new Score(0, 2, 0).addMove(10, 0).getScore(), is(11));
        assertThat(new Score(0, 2, 0).addMove(9, 0).getScore(), is(9));
    }
}
