package model;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

import algo.SearchNode;

/**
 * Geometry helper
 */
public class Geometry {
    public static List<Move> path(int x1, int y1, int x2, int y2) {
        List<Move> path = new ArrayList<>();
        while(y1!=y2) {
            Move m;
            if (y1<y2) {
                m = Move.SE;
            } else {
                m = Move.NW;
            }
            path.add(m);
            x1 = x1 + m.dx(y1);
            y1 = y1 + m.dy(y1);
        }
        while(x1!=x2) {
            Move m;
            if (x1<x2) {
                m = Move.E;
            } else {
                m = Move.W;
            }
            path.add(m);
            x1 = x1 + m.dx(y1);
            y1 = y1 + m.dy(y1);
        }
        return path;
    }

    public static Point rotateCW(Point point, Point pivot) {
        List<Move> path = path(pivot.x, pivot.y, point.x, point.y);
        int x = pivot.x;
        int y = pivot.y;
        for(Move m : path) {
            x = x + m.cw().dx(y);
            y = y + m.cw().dy(y);
        }
        return new Point(x,y);
    }

    public static Point rotateCCW(Point point, Point pivot) {
        List<Move> path = path(pivot.x, pivot.y, point.x, point.y);
        int x = point.x;
        int y = point.y;
        for(Move m : path) {
            x = x + m.ccw().dx(y);
            y = y + m.ccw().dy(y);
        }
        return new Point(x,y);
    }

    public static String fromPoints(List<Point> points, int minX, int minY) {
        StringBuilder builder = new StringBuilder();
        for(int y=minY;y<=minY+20;y++) {
            if (Math.abs(y)%2==1) builder.append(" ");
            for(int x=minX;x<=minX+20;x++) {
                if (points.contains(new Point(x,y))) {
                    builder.append("* ");
                } else {
                    builder.append(". ");
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }
    
    public static int distance(int ax, int ay, int bx, int by) {
    	//convert to cube
		int cax = ax - (ay - (ay&1)) / 2;
		int caz = ay;
		int cay = -cax-caz;
		
		int cbx = bx - (by - (by&1)) / 2;
		int cbz = by;
		int cby = -cbx-cbz;
		
		return cubeDistance(cax, cay, caz, cbx, cby, cbz);
    }
    
    private static int cubeDistance(int ax, int ay, int az, int bx, int by, int bz) {
        return (abs(ax - bx) + abs(ay - by) + abs(az - bz)) / 2;
    }

	public static int dist(SearchNode to, SearchNode sn) {
		int x1 = to.getBoard().getTrueSourceX();
        int y1 = to.getBoard().getTrueSourceY();
        int a1 = to.getBoard().getSourceAngle();
        
        int x2 = sn.getBoard().getTrueSourceX();
        int y2 = sn.getBoard().getTrueSourceY();
        int a2 = sn.getBoard().getSourceAngle();
        
        if (y2 > y1) { //TODO: we ignore rotations here, not sure how costly it is
        	return Integer.MAX_VALUE/2;
        }

        int dist = Geometry.distance(x1, y1, x2, y2) + angleDistance(a1, a2);
		return dist;
	}
	
	public static int angleDistance(int a1, int a2) {
		return Math.min(abs(a1-a2), abs(6-a1+a2) % 6);
	}
}