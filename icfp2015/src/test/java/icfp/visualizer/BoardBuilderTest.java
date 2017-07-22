package icfp.visualizer;

import bitsets.Bitsets;
import com.google.common.collect.ImmutableList;
import model.Board;
import model.StaticBoardInfo;
import model.UnitTemplate;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static icfp.visualizer.BoardCell.cell;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BoardBuilderTest {

    @Test
    public void shouldCreateCorrectRepresentationOfUnselectedBoard(){
        BoardBuilder modelBuilder = new BoardBuilder();
        Board board = Board.initialBoard(null, new StaticBoardInfo(2, 3, ImmutableList.copyOf(new ArrayList<>())));
        List<BoardCell> cells = modelBuilder.build(board);
        assertEquals(6, cells.size());
        assertThat(cells, CoreMatchers.hasItems(cell(0, 0, false, false), cell(1, 0, false, false), cell(0, 1, false, false), cell(1, 1, false, false), cell(0, 2, false, false), cell(1, 2, false, false)));
    }

    @Test
    public void shouldCreateCorrectRepresentationOfSelectedCells(){
        BoardBuilder modelBuilder = new BoardBuilder();
        BitSet selectedCells = Bitsets.from(".*****");
        Board board =  Board.initialBoard(selectedCells, new StaticBoardInfo(2, 3, ImmutableList.copyOf(new ArrayList<>())));
        List<BoardCell> cells = modelBuilder.build(board);
        assertEquals(6, cells.size());
        assertThat(cells, CoreMatchers.hasItems(cell(0, 0, false, false), cell(1, 0, true, false), cell(0, 1, true, false), cell(1, 1, true, false), cell(0, 2, true, false), cell(1, 2, true, false)));
    }
}