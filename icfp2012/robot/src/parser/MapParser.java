package parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.Cell;

public class MapParser {

    private final Input input;

    public MapParser(Input input) {
        this.input = input;
    }

    public MineInfo readMap() {
        List<Collection<Cell>> map = new ArrayList<Collection<Cell>>();
        int height = 0;
        int maxWidth = 0;
        String line;
        while (true) {
            line = input.readLine();
            if (line==null || line.isEmpty()) {
                break;
            }
            height++;
            int length = line.length();
            Collection<Cell> cellLine = new ArrayList<Cell>(length);
            for (int cptr = 0; cptr < length; cptr++) {
                cellLine.add(Cell.fromEncoding((byte)line.charAt(cptr)));
            }
            maxWidth = Math.max(maxWidth, length);
            map.add(cellLine);
        }
        Collection<Cell> cells = new ArrayList<Cell>(height*maxWidth);

        for(int reversePtr = map.size()-1;reversePtr>=0;reversePtr--) {
            Collection<Cell> cellLine = map.get(reversePtr);
            cells.addAll(cellLine);
            for(int i=cellLine.size();i<maxWidth;i++) {
                cells.add(Cell.EMPTY);
            }
        }
        return new MineInfo(cells.toArray(new Cell[cells.size()]), maxWidth);
    }

    public static class MineInfo {
        public final Cell[] cells;
        public final int width;

        public MineInfo(Cell[] cells, int width) {
            this.cells = cells;
            this.width = width;
        }
    }
}
