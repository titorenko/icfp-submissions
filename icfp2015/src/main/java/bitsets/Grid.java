package bitsets;

import java.util.BitSet;

/**
 *
 */
public class Grid {
    private final BitSet cells;
    private final int width;
    private final int height;

    public Grid(BitSet cells, int width, int height) {
        this.cells = cells;
        this.width = width;
        this.height = height;
    }

    public String asFourGrid() {
        StringBuilder builder = new StringBuilder();
        for(int y=0;y<height;y++) {
            for(int x=0;x<width;x++) {
                if (cells.get(y*width+x)) {
                    builder.append("*");
                } else {
                    builder.append(".");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String asSixGrid() {
        StringBuilder builder = new StringBuilder();
        for(int y=0;y<height;y++) {
            if (y%2!=0) {
                builder.append(" ");
            }
            for(int x=0;x<width;x++) {
                if (cells.get(y*width+x)) {
                    builder.append("* ");
                } else {
                    builder.append(". ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String asBothGrids() {
        StringBuilder builder = new StringBuilder();
        for(int y=0;y<height;y++) {
            if (y%2!=0) {
                builder.append(" ");
            }
            for(int x=0;x<width;x++) {
                if (cells.get(y*width+x)) {
                    builder.append("* ");
                } else {
                    builder.append(". ");
                }
            }
            if (y%2==0) {
                builder.append(" ");
            }
            builder.append("   ");
            for(int x=0;x<width;x++) {
                if (cells.get(y*width+x)) {
                    builder.append("*");
                } else {
                    builder.append(".");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println("Event lines");
        String fig1 =
                "...." +
                "..*." +
                "..*." +
                "....";
        printFigure(fig1);
        String fig2 =
                "...." +
                "...." +
                ".**." +
                "....";
        printFigure(fig2);
        String fig3 =
                "...." +
                "*..." +
                ".*.." +
                "....";
        printFigure(fig3);
        String fig4 =
                ".*.." +
                "*..." +
                "...." +
                "....";
        printFigure(fig4);
        String fig5 =
                ".**." +
                "...." +
                "...." +
                "....";
        printFigure(fig5);
        String fig6 =
                "..*." +
                "..*." +
                "...." +
                "....";
        printFigure(fig6);
        System.out.println("Odd lines");
        String fig7 =
                "...." +
                "...." +
                "..*." +
                ".*..";
        printFigure(fig7);
        String fig8 =
                "...." +
                "...." +
                "...." +
                "**..";
        printFigure(fig8);
        String fig9 =
                "...." +
                "...." +
                "*..." +
                "*...";
        printFigure(fig9);
        String fig10 =
                "...." +
                "*..." +
                "*..." +
                "....";
        printFigure(fig10);
        String fig11 =
                "...." +
                "**.." +
                "...." +
                "....";
        printFigure(fig11);
        String fig12 =
                "...." +
                ".*.." +
                "..*." +
                "....";
        printFigure(fig12);

    }

    private static void printFigure(String fig1) {
        Grid grid = new Grid(Bitsets.from(fig1), 4, 4);
        System.out.println("6 4 Grid");
        System.out.print(grid.asBothGrids());
//        System.out.println("6 Grid");
//        System.out.print(grid.asSixGrid());
    }
}
