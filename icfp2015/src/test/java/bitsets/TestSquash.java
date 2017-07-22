package bitsets;

import org.junit.Test;

import java.util.BitSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class TestSquash {

    @Test
    public void testClear() {
        String boardStr =
                "..*..." +
                "......" +
                "......" +
                "......" +
                "......" +
                "******";

        String boardStr2 =
                "......\n" +
                " ..*...\n" +
                "......\n" +
                " ......\n" +
                "......\n" +
                " ......";
        BitSet board = Bitsets.from(boardStr);
        int count = Bitsets.squash(board, 6, 6);
        assertThat(Bitsets.toString(board, 6, 6), is(boardStr2));
        assertThat(count, is(1));
    }

    @Test
    public void testClear2() {
        String boardStr =
                "..*..." +
                "......" +
                "......" +
                "******" +
                "......" +
                "******";
        String boardStr2 =
                "......\n" +
                " ......\n" +
                "..*...\n" +
                " ......\n" +
                "......\n" +
                " ......";
        BitSet board = Bitsets.from(boardStr);
        int count = Bitsets.squash(board, 6, 6);
        assertThat(Bitsets.toString(board, 6, 6), is(boardStr2));
        assertThat(count, is(2));
    }

    @Test
    public void testClear3() {
        String boardStr =
                "..*..." +
                "......" +
                "......" +
                "******" +
                "......" +
                "**.***";
        String boardStr2 =
                "......\n" +
                " ..*...\n" +
                "......\n" +
                " ......\n" +
                "......\n" +
                " **.***";
        BitSet board = Bitsets.from(boardStr);
        int count = Bitsets.squash(board, 6, 6);
        assertThat(Bitsets.toString(board, 6, 6), is(boardStr2));
        assertThat(count, is(1));
    }
}
