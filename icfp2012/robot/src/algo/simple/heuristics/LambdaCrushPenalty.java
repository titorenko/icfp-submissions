package algo.simple.heuristics;

import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class LambdaCrushPenalty implements Heuristic {

    @Override
    public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
        int nLambdasCrushed = mine.getInitialAllLambdaCount() - (mine.getRobot().getLambdaCollected() + mine.getUncollectedLambdaCount());
        if (nLambdasCrushed > 0) {
        	return new Delta(-(mine.getInitialAllLambdaCount() * 25 + nLambdasCrushed * 75), true);
        }
        return Delta.NULL;
    }

}
