package icfp.io.model;

import model.Point;

public class Cell {
    public int x;
    public int y;

    public Cell() {
    }

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int cellIndex(int width) {
        return x+y*width;
    }

    public Point toPoint() {
        return new Point(x,y);
    }

    @Override
    public String toString() {
        return "{ " + x + ", " + y + "}";
    }
}
