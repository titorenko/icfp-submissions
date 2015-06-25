package algo.simple.heuristics;

import java.util.List;

import model.Cell;
import model.Coordinate;
import model.Mine;

public class HoLambdaBlockedPenalty implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		List<Integer> hoLambdaCells = mine.getHoLambdaCells();
		int nBlocked = 0;
		for (Integer hoIdx : hoLambdaCells) {
			if (isBlocked(mine, hoIdx)) nBlocked++;
		}
		if (nBlocked > 0) return new Delta(-(nBlocked*75  + mine.getInitialAllLambdaCount()*25), true);
		return Delta.NULL;
	}

	private boolean isBlocked(Mine mine, Integer hoIdx) {
		Coordinate coord = mine.toCoord(hoIdx);
		Cell downCell = mine.get(coord.x, coord.y - 1);
		if (downCell.isHardBarier()) {
			Cell leftCell = mine.get(coord.x-1, coord.y);
			if (leftCell.isHardBarier()) return true;
			Cell rightCell = mine.get(coord.x+1, coord.y);
			if (rightCell.isHardBarier()) return true;
		}
		return false;
	}

}
