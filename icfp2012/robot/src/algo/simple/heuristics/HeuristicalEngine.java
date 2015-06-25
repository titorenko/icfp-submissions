package algo.simple.heuristics;

import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class HeuristicalEngine {
	
	private final Heuristic[] dynamicEuristics;
	private final Heuristic[] staticEuristics;
	private final int nullMoveLookAhead;
	
	public HeuristicalEngine(Mine mine, Heuristic[] dynamicEuristics, Heuristic[] staticEuristics) {
		this.dynamicEuristics = dynamicEuristics;
		this.staticEuristics = staticEuristics;
		this.nullMoveLookAhead = mine.getHeight() - 1;
	}
	
	public Delta applyHeuristics(Mine game, HeuristicEngineContext ctx) {
		Delta euristicsDelta = getEuristicsDelta(game, staticEuristics, ctx);
		euristicsDelta = euristicsDelta.add(applyDynamicHeuristics(game, ctx));
		return new Delta(game.getScore()).add(euristicsDelta);
	}

	public Delta applyDynamicHeuristics(Mine mine, HeuristicEngineContext ctx) {
		Delta minValue = Delta.MAX_VALUE;
		if (mine.getLambdaCells().size() > 0) {
			mine = mine.removeRobot();
		}
		for (int i = 0; i < nullMoveLookAhead; i++) {
			Delta euristicsDelta = getEuristicsDelta(mine, dynamicEuristics, ctx);
			if (euristicsDelta.isLessThan(minValue)) {
				minValue = euristicsDelta;
			}
			Mine newMine = mine.makeNullMove();
			if (mine.hashCode() == newMine.hashCode()) {
				return minValue;
			}
			mine = newMine;
		}
		return minValue;
	}

	private Delta getEuristicsDelta(Mine game, Heuristic[] euristics, HeuristicEngineContext ctx) {
		Delta euristicsDelta = Delta.NULL;
		for (Heuristic euristic : euristics) {
			Delta delta = euristic.getDelta(game, ctx);
			euristicsDelta = euristicsDelta.add(delta);
		}
		return euristicsDelta;

	}

	public Delta applyHeuristics(Mine mine) {
		return applyHeuristics(mine, new HeuristicEngineContext());
	}
}
