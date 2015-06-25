package algo.simple.heuristics;


import java.util.List;

import model.Mine;

public class LambdaFloodedPenalty implements Heuristic {

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		
		double floodLevel = getFloodLevel(mine); 
		int width = mine.getWidth();
		int waterproof = mine.getCfg().getWaterproof();

		List<Integer> lambdaCells = mine.getLambdaCells();
		int penaltyBase = mine.getInitialAllLambdaCount() * 2 + mine.getRobot().getLambdaCollected();
		
		int delta = 0;
		boolean isUnreachable = false;
		for (int lambda : lambdaCells) {
			int lambdaY = lambda / width;
			double lambdaFlooding = floodLevel - lambdaY;
			double floodingFactor = Math.min(0, lambdaFlooding);
			
			delta -= penaltyBase * Math.exp(floodingFactor);
			isUnreachable = isUnreachable || (lambdaFlooding > waterproof - 1);
		}
		
		return new Delta(delta, isUnreachable);
	}

	double getFloodLevel(Mine mine) {
		int floodLevel = mine.getFloodingState().getWaterLevel();
		double timeToNextBump = mine.getFloodingState().getTimeToNextBump();
		double floodRate = mine.getCfg().getFloodingRate();
		return floodLevel + (floodRate-timeToNextBump)/floodRate;
	}

}