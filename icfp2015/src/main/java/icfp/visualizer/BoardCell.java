package icfp.visualizer;

/**
 * Created by Olya on 07/08/2015.
 */
public class BoardCell {
    private final int column;
    private final int row;
    private final boolean inBitSets;
    private final boolean inUnit;

    public BoardCell(int column, int row, boolean inBitSets, boolean inUnit) {
        this.column = column;
        this.row = row;
        this.inBitSets = inBitSets;
        this.inUnit = inUnit;
    }

    public static BoardCell cell(int column, int row, boolean inBitSets, boolean inUnit){
        return new BoardCell(column, row, inBitSets, inUnit);
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardCell cell = (BoardCell) o;

        if (column != cell.column) return false;
        if (inBitSets != cell.inBitSets) return false;
        if (inUnit != cell.inUnit) return false;
        if (row != cell.row) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        result = 31 * result + (inBitSets ? 1 : 0);
        result = 31 * result + (inUnit ? 1 : 0);
        return result;
    }

    public boolean isInUnit() {
        return inUnit;
    }

    public boolean isInBitSets() {
        return inBitSets;
    }
}
