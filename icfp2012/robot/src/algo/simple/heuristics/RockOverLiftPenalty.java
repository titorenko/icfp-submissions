package algo.simple.heuristics;

import model.Cell;
import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class RockOverLiftPenalty implements Heuristic {
	private final static Delta PENALTY = new Delta(-100);//slightly bigger than lambda 

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int liftCell = mine.getLiftCell();
		int liftX = liftCell % mine.getWidth();
		int liftY = liftCell / mine.getWidth();
		for (int y = liftY+1; y < mine.getHeight()-1; y++) {
			Cell above = mine.get(liftX, y);
			if (above == Cell.ROCK) return PENALTY;
			if (above == Cell.EARTH || above == Cell.WALL || above == Cell.NULL) break;
		}
		return Delta.NULL;
	}

}
