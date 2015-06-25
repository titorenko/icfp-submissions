package algo.simple.heuristics;

import java.util.ArrayList;
import java.util.List;

import model.Cell;
import model.Coordinate;
import model.Mine;

public class ClosedLambdaLiftPenalty implements Heuristic {
	

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		List<Integer> cells = new ArrayList<Integer>(mine.getLambdaCells());
		if (mine.getLiftCell() != -1) cells.add(mine.getLiftCell());
		int nClosed = 0;
		for (Integer cellIdx : cells) {
			if (isClosed(mine, cellIdx)) {
				nClosed++;
			}
		}
	
		if (nClosed == 0) return Delta.NULL;
		int score = mine.getInitialAllLambdaCount() * 25 + nClosed*25;
		Delta delta = new Delta(-score, true);
		return delta;
	}

	private boolean isClosed(Mine mine, Integer cellIdx) {
		Coordinate coord = mine.toCoord(cellIdx);
		Cell cell = mine.get(coord.x-1, coord.y);
		if (isOpen(cell)) return false;
		
		cell = mine.get(coord.x+1, coord.y);
		if (isOpen(cell) && isSideRockMoveable(mine, cell, coord.x-1, coord.y)) return false;
		
		cell = mine.get(coord.x, coord.y-1);
		if (isOpen(cell) && isSideRockMoveable(mine, cell, coord.x+1, coord.y)) return false;
		
		cell = mine.get(coord.x, coord.y+1);
		if (isOpen(cell) && isUpperRockMoveable(mine, cell, coord.x, coord.y+1)) return false;
		
		return true;
	}


	private boolean isSideRockMoveable(Mine mine, Cell cell, int x, int y) {
		if (!cell.isHardFallingThing()) return true;
		if (!isOpen(mine.get(x, y-1))) return false;
		return true;
	}

	private boolean isUpperRockMoveable(Mine mine, Cell cell, int x, int y) {
		if (cell.isHardFallingThing()) {
			Cell left = mine.get(x-1, y);
			Cell right = mine.get(x+1, y);
			if (!isOpen(left)) return false;
			if (!isOpen(right)) return false;
			if (left.isHardFallingThing() && !isOpen(mine.get(x-1, y-1))) return false;
			if (right.isHardFallingThing() && !isOpen(mine.get(x+1, y-1))) return false;
			return isOpen(left) && isOpen(right);
		}
		return true;
	}

	private boolean isOpen(Cell cell) {
		return cell!=Cell.WALL && cell!= Cell.NULL; 
	}
}