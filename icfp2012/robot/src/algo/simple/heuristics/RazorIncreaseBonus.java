package algo.simple.heuristics;

import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class RazorIncreaseBonus implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int beardCount = mine.getBeardState().getBeardCount();
		return beardCount > 0 ? new Delta(beardCount*mine.getRobot().getRazorCount()) : Delta.NULL;
	}
}
