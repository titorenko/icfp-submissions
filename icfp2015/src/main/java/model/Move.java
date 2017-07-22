package model;

/**
 * Moves on 4 mapped hex field
 */
public enum Move {
    // order is important for cw, ccw
    E  ( 1, 0, 1, 0),
    SE ( 0, 1, 1, 1),
    SW (-1, 1, 0, 1),
    W  (-1, 0,-1, 0),
    NW (-1,-1, 0,-1),
    NE ( 0,-1, 1,-1);

    int dx[];
    int dy[];

    Move(int dx1, int dy1, int dx2, int dy2) {
        this.dx = new int[]{dx1, dx2};
        this.dy = new int[]{dy1, dy2};
    }

    public int dx(int x) {
        return dx[Math.abs(x) % 2];
    }
    
    public int dy(int y) {
        return dy[Math.abs(y) % 2];
    }

    public Move cw() {
        return values()[(ordinal()+1)%values().length];
    }

    public Move ccw() {
        return values()[(ordinal()+values().length-1)%values().length];
    }
}
