package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit template. It could be shared by unit positions created on search steps
 * Mapping of 6 unit onto 4 grid for all rotations
 */
public class UnitTemplate {
    
    public class UnitRotation {
        List<Point> points;
        int[] sortedYs;
        int ysum;
        int size; 
        
        UnitRotation(List<Point> points) {
            this.points = points;
            this.ysum = points.stream().mapToInt(p -> (p.y+offsetY)).sum();
            this.size = points.size();
            List<Integer> sortedYs = points.stream().map(p -> p.y).sorted().collect(Collectors.toList());
            this.sortedYs = new int[sortedYs.size()];
            Iterator<Integer> it = sortedYs.iterator();
            for (int i = 0; i < this.sortedYs.length; i++) {
                this.sortedYs[i] = it.next();
            }
        }
        
        public List<Point> getPoints() {
            return points;
        }

        public int size() {
            return size;
        }
    }

    private List<UnitRotation> rotations;
    private List<Integer> allowedAngles;
    
    // offset for top center point to center of glass
    private int offsetX;
    private int offsetY;

    public UnitTemplate(List<List<Point>> rotations, int offsetX, int offsetY) {
		this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.allowedAngles = nonDuplicateAngles(rotations);
        this.rotations = rotations.stream().map(UnitRotation::new).collect(Collectors.toList());
    }

    private List<Integer> nonDuplicateAngles(List<List<Point>> rotations) {
    	Set<Set<Point>> seenRotations = new HashSet<>();
    	List<Integer> allowedAngles = new ArrayList<>();
		for (int index=0;index<6;index++) {
			List<Point> r = rotations.get(index);
            Set<Point> s = new HashSet<>(r);
			if (!seenRotations.contains(s)) {
				allowedAngles.add(index);
			}
			seenRotations.add(s);
		}
    	return allowedAngles;
	}

	// for debug purposes
    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    // would be used internally
    public UnitRotation setFor(int y, int angle) {
        return rotations.get(angle%6 + (y%2*6));
    }

	public boolean canMoveTo(Board board, int sourceAngle, int sourceX, int sourceY) {
        List<Point> points = setFor(sourceY, sourceAngle).points;
        for(Point point : points) {
            if (board.isSet(sourceX+point.x+offsetX, sourceY+point.y+offsetY)) {
                return false;
            }
        }
        return true;
	}

	// for debug use only. mimics placement.
	public Set<Point> pointsAtLocation(int sourceAngle, int sourceX, int sourceY) {
        List<Point> points = setFor(sourceY, sourceAngle).points;
		Set<Point> newPoints = new HashSet<>();
        for(Point point : points) {
			newPoints.add(new Point(sourceX+point.x+offsetX, sourceY+point.y+offsetY));
        }
        return newPoints;
	}

    public void updateBitSet(BitSet board, int width, int sourceAngle, int sourceX, int sourceY) {
        List<Point> points = setFor(sourceY, sourceAngle).points;
        for(Point point : points) {
            int x = sourceX+point.x+offsetX;
            int y = sourceY+point.y+offsetY;
            board.set(x+y*width);
        }
        
    }

	public int getSize() {
		return rotations.get(0).size();
	}

	public int nextAngle(int a) {
		int index = allowedAngles.indexOf(a);
		int nextIndex = index == (allowedAngles.size()-1) ? 0 : index+1;
		return allowedAngles.get(nextIndex);
	}

	public int prevAngle(int a) {
		int index = allowedAngles.indexOf(a);
		int prevIndex = index == 0 ? allowedAngles.size()-1 : index-1;
		return allowedAngles.get(prevIndex);
	}
	
	public List<Integer> getAllowedAngles() {
		return allowedAngles;
	}
	
    public int getYSum(Board board) { 
        int y = board.getSourceY();
        int a = board.getSourceAngle();
    	UnitRotation ur = setFor(y, a);
    	return ur.ysum + ur.size * y;
    }
    
    public int getFilledNeighbourCount(Board board) {
    	int sourceX = board.getSourceX();
    	int sourceY = board.getSourceY();
    	int sourceAngle = board.getSourceAngle();
    	List<Point> points = setFor(sourceY, sourceAngle).points;
    	int count = 0;
        for(Point point : points) {
            int x = sourceX+point.x+offsetX;
            int y = sourceY+point.y+offsetY;
            count += board.getFilledNeighbourCount(x, y);
        }
        return count;
    }

    public int getPlacementRowsFill(Board board) {
        int sourceY = board.getSourceY();
        int sourceAngle = board.getSourceAngle();
        int[] ys = setFor(sourceY, sourceAngle).sortedYs;
        
        int sum = 0;
        int prevY = Integer.MIN_VALUE;
        for (int y : ys) {
            if (y == prevY) {
                sum++;
            } else {
                sum += board.getRow(sourceY+y+offsetY).cardinality()+1;
                prevY = y;
            }
        }
        return sum;
    }
}