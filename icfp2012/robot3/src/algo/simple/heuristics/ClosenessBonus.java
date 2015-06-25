package algo.simple.heuristics;

import model.Mine;

public class ClosenessBonus implements Heuristic {
	
	private final int targetX;
	private final int targetY;

	public ClosenessBonus(Mine mine, int targetSquare) {
		this.targetX = targetSquare % mine.getWidth();
		this.targetY = targetSquare / mine.getWidth();
	}

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		int distance = mine.getRobot().distanceTo(targetX, targetY);
		return new Delta(-distance);
	}

}
