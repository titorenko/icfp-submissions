package algo.simple.heuristics;

import model.Mine;

public class LambdaCrushPenalty implements Heuristic {
	
	
    @Override
    public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
        int nLambdasCrushed = mine.getInitialAllLambdaCount() - (mine.getRobot().getLambdaCollected() + mine.getUncollectedLambdaCount());
        if (nLambdasCrushed > 0) {
        	return new Delta(-(mine.getInitialAllLambdaCount() * 25 + nLambdasCrushed * 75), true);
        } else {
        	/*int hoInitial = mine.getInitialAllLambdaCount() - mine.getInitialLambdaCount();
        	int hoNow = mine.getHigherOrderLambaCount();
        	int deltaHo = hoInitial - hoNow;
        	return new Delta(deltaHo*5);*/
        	return Delta.NULL;
        }
    }

}
