package algo.simple.heuristics;

import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public interface Heuristic {
	Delta getDelta(Mine mine, HeuristicEngineContext ctx);

}
