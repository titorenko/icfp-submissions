package algo.simple.heuristics;

import java.util.List;

import model.Mine;

public class LambdaClosenessBonus implements Heuristic {
	

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int minDistance = Integer.MAX_VALUE;
		List<Integer> lambdaCells = mine.getLambdaCells();
		if (lambdaCells.size() == 0) return Delta.NULL;
		for (Integer lambda : lambdaCells) {
			if (ctx.isClosedSquare(lambda)) continue;
			int lambdaX = lambda % mine.getWidth();
			int lambdaY = lambda / mine.getWidth();
			int distance = mine.getRobot().distanceTo(lambdaX, lambdaY);
			if (distance < minDistance) minDistance = distance;
		}
		return new Delta(-minDistance);
	}

}
