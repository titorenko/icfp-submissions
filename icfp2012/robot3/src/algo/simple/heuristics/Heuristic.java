package algo.simple.heuristics;

import model.Mine;

public interface Heuristic {
	Delta getDelta(Mine mine, HeuristicEngineContext ctx);

}
