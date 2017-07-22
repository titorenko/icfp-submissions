package icfp.visualizer;

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import bitsets.Bitsets;
import model.Board;

import static icfp.visualizer.BoardCell.cell;

public class BoardBuilder {

    public List<BoardCell> build(Board board) {
        BitSet selectedCells;
        if(board.getCells() == null){
            selectedCells = new BitSet();
        } else {
            selectedCells = (BitSet) board.getCells().clone();
            if (!board.isFinished()) {
                board.getTemplate().updateBitSet(selectedCells, board.getWidth(), board.getSourceAngle(), board.getSourceX(), board.getSourceY());
            }
        }
        return IntStream.range(0, (board.getWidth() * board.getHeight())).mapToObj(index -> getCell(board, selectedCells, index)).collect(Collectors.toList());
    }

    private BoardCell getCell(Board board, BitSet selectedCells, int index) {
        int column = index % board.getWidth();
        int row = index / board.getWidth();
        boolean isSelected = selectedCells.get(index);
        //TODO
        boolean isInUnit = false;
        return cell(column, row, isSelected, isInUnit);
    }

    public String toString(Board board) {
        if (board.isFinished()) {
            return "F\n"+ Bitsets.toString(board.getCells(), board.getWidth(), board.getHeight());
        } else {
            BitSet newCells = (BitSet) board.getCells().clone();
            board.getTemplate().updateBitSet(newCells, board.getWidth(), board.getSourceAngle(), board.getSourceX(), board.getSourceY());
            return Bitsets.toString(newCells, board.getWidth(), board.getHeight());
        }
    }
}
