package algo.simple.heuristics;

import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class RobotUnderwaterPenalty implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		return new Delta(-mine.getFloodingState().getUnderwaterTime()/2);
	}

}
