package icfp.io;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import icfp.io.model.Problem;
import model.Board;

public class TestModelGeneration {
    @Test
    public void loadModel() {
        Problem problem = new Parser().parse("/problems/problem_2.json");
        List<Board> boards = problem.createBoards();
        assertThat(boards.size(), is(10));
        assertThat(boards.get(0).getHeight(), is(30));
        assertThat(boards.get(0).getWidth(), is(15));
        //assertThat(boards.get(0).getSource().size(), is(100));
       /* assertThat(boards.get(0).getSource().get(0), is(
                new Unit(3, 5, 0, 0, 1, 2,Bitsets.from(
                                "..*" +
                                ".*." +
                                ".*." +
                                "*.." +
                                "*.."
                ))
        ));*/
    }
}
