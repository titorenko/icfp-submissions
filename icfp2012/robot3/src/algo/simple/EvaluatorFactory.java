package algo.simple;

import java.util.ArrayList;
import java.util.Collection;

import model.Cell;
import model.Mine;
import algo.simple.heuristics.ClosedRobotPenalty;
import algo.simple.heuristics.Heuristic;
import algo.simple.heuristics.HeuristicalEngine;
import algo.simple.heuristics.LambdaClosenessBonus;
import algo.simple.heuristics.LambdaCrushPenalty;
import algo.simple.heuristics.LambdaFloodedPenalty;
import algo.simple.heuristics.OpenLiftClosenessBonus;
import algo.simple.heuristics.RazorIncreaseBonus;
import algo.simple.heuristics.RobotUnderwaterPenalty;
import algo.simple.heuristics.RockOverLiftPenalty;

public class EvaluatorFactory {
	 
	public static Evaluator getDefaultEvaluator(Mine mine) {
		return getEvaluator(mine, getEngine(mine));
    }
	
	public static Evaluator getEvaluator(Mine mine, HeuristicalEngine engine) {
		return new Evaluator(mine, engine);
	}
	
	public static HeuristicalEngine getEngine(Mine mine) {
		Collection<Heuristic> dynamicHeuristics = getDefaultDynamic(mine);
		Collection<Heuristic> staticHeuristics = getDefaultStatic(mine);
		return new HeuristicalEngine(mine, dynamicHeuristics.toArray(new Heuristic[0]), staticHeuristics.toArray(new Heuristic[0]));
	}
	

	private static Collection<Heuristic> getDefaultStatic(Mine mine) {
		Collection<Heuristic> staticHeuristics = new ArrayList<Heuristic>();
	    staticHeuristics.add(new OpenLiftClosenessBonus());
	    staticHeuristics.add(new ClosedRobotPenalty(mine));
	    staticHeuristics.add(new RockOverLiftPenalty());
	    if (mine.getCfg().isFloodingActive()) {
	    	staticHeuristics.add(new LambdaFloodedPenalty());
	    	staticHeuristics.add(new RobotUnderwaterPenalty());
	    } 
	    if (mine.getBeardState().getBeardCount() > 0) {
	    	//staticHeuristics.add(new BeardGrowthPenalty());
	    	staticHeuristics.add(new RazorIncreaseBonus());
	    }
	    if (mine.getCellCount(Cell.HO_LAMBDA) > 0) {
	    	staticHeuristics.add(new LambdaCrushPenalty());
	    }
	    staticHeuristics.add(new LambdaClosenessBonus());
	    //staticHeuristics.add(new ClosedLambdaLiftPenalty(mine));
		return staticHeuristics;
	}

	private static Collection<Heuristic> getDefaultDynamic(Mine game) {
		Collection<Heuristic> dynamicHeuristics = new ArrayList<Heuristic>();
		//dynamicHeuristics.add(new ClosedLambdaLiftPenalty(game));
		return dynamicHeuristics;
	}

	public static EvaluationContext getContext(Mine mine) {
		return new EvaluationContext(getEngine(mine));
	}


}
