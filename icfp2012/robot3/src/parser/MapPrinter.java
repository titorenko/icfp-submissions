package parser;

import model.Cell;
import model.Mine;

public class MapPrinter {

    private final Output writer;

    public MapPrinter(Output writer) {
        this.writer = writer;
    }

    public void printMap(Mine mine) {
        int width = mine.getWidth();
        int height = mine.getHeight();

        for (int y = height-1; y >= 0; y--) {
            StringBuilder buffer = new StringBuilder(width);
            for (int x = 0; x < width; x++) {
                Cell cell = mine.get(x, y);
                buffer.append(cell.toString());
            }
            writer.printLine(buffer.toString());
        }
    }
}