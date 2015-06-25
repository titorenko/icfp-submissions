/*package algo.simple.heuristics;

import java.util.List;

import model.Cell;
import model.Mine;

public class BeardClosenessBonus implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int razorCount = mine.getRobot().getRazorCount();
		if (razorCount == 0) return Delta.NULL;
		List<Integer> beards = mine.findCells(Cell.BEARD);
		if (beards.size() == 0) return Delta.NULL;
		int minDistance = Integer.MAX_VALUE;
		for (Integer beard : beards) {
			int beardX = beard % mine.getWidth();
			int beardY = beard / mine.getWidth();
			int distance = mine.getRobot().distanceTo(beardX, beardY);
			if (distance < minDistance) minDistance = distance;
		}
		return new Delta(-minDistance*beards.size());
	}

}
*/