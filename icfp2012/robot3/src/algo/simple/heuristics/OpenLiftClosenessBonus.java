package algo.simple.heuristics;

import model.Cell;
import model.Mine;

public class OpenLiftClosenessBonus implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int liftIdx = mine.getLiftCell();
		Cell liftCell = mine.unsafeGet(liftIdx);
		if (liftCell == Cell.CLOSED_LIFT) return Delta.NULL;
		int liftX = liftIdx % mine.getWidth();
		int liftY = liftIdx / mine.getWidth();
		int distance = mine.getRobot().distanceTo(liftX, liftY)+1;
		return new Delta((int) (mine.getRobot().getLambdaCollected()*(50.0/distance)));
	}		

}
