package algo.mst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Cell;
import model.Mine;
import algo.simple.heuristics.ClosedLambdaLiftPenalty;
import algo.simple.heuristics.ClosedRobotPenalty;
import algo.simple.heuristics.ClosenessBonus;
import algo.simple.heuristics.Heuristic;
import algo.simple.heuristics.HeuristicalEngine;
import algo.simple.heuristics.HoLambdaBlockedPenalty;
import algo.simple.heuristics.LambdaClosenessBonus;
import algo.simple.heuristics.LambdaCrushPenalty;
import algo.simple.heuristics.LambdaFloodedPenalty;
import algo.simple.heuristics.OpenLiftClosenessBonus;
import algo.simple.heuristics.RazorIncreaseBonus;
import algo.simple.heuristics.RobotUnderwaterPenalty;
import algo.simple.heuristics.RockOverLiftPenalty;

public class HeuristicalEngineFactory {
	 
	public static HeuristicalEngine getEngine(Mine mine) {
		List<Heuristic> staticHeuristics = new ArrayList<Heuristic>();
	    staticHeuristics.add(new OpenLiftClosenessBonus());
	    staticHeuristics.add(new ClosedRobotPenalty(mine));
	    staticHeuristics.add(new RockOverLiftPenalty());
	    if (mine.getCfg().isFloodingActive()) {
	    	staticHeuristics.add(new LambdaFloodedPenalty());
	    	staticHeuristics.add(new RobotUnderwaterPenalty());
	    } 
	    if (mine.getBeardState().getBeardCount() > 0) {
	    	staticHeuristics.add(new RazorIncreaseBonus());
	    }
	    if (mine.getCellCount(Cell.HO_LAMBDA) > 0) {
	    	staticHeuristics.add(new LambdaCrushPenalty());
	    	staticHeuristics.add(new HoLambdaBlockedPenalty());
	    }
	    staticHeuristics.add(new LambdaClosenessBonus());
	    staticHeuristics.add(new ClosedLambdaLiftPenalty());
	    
	    List<Heuristic> dynamicHeuristics = new ArrayList<Heuristic>();
	    //dynamicHeuristics.add(new ClosedLambdaLiftPenalty(mine));
		
		return new HeuristicalEngine(mine, dynamicHeuristics, staticHeuristics);
	}
	
	public static HeuristicalEngine getPessimisticEngineForAStarEval(Mine mine, int targetLambda) {
		List<Heuristic> staticHeuristics = new ArrayList<Heuristic>();
	    
		staticHeuristics.add(new ClosedRobotPenalty(mine));
	    staticHeuristics.add(new RockOverLiftPenalty());
	    staticHeuristics.add(new ClosedLambdaLiftPenalty());
	    staticHeuristics.add(new ClosenessBonus(mine, targetLambda));
	    
	    if (mine.getCfg().isFloodingActive()) {
	    	staticHeuristics.add(new RobotUnderwaterPenalty());
	    } 
	    if (mine.getBeardState().getBeardCount() > 0) {
	    	staticHeuristics.add(new RazorIncreaseBonus());
	    }
	    if (mine.getCellCount(Cell.HO_LAMBDA) > 0) {
	    	staticHeuristics.add(new LambdaCrushPenalty());
	    	staticHeuristics.add(new HoLambdaBlockedPenalty());
	    }
	    
	    List<Heuristic> dynamicHeuristics = new ArrayList<Heuristic>();
	    //dynamicHeuristics.add(new ClosedLambdaLiftPenalty(mine));
	    
		return new HeuristicalEngine(mine, dynamicHeuristics, staticHeuristics);
	}
	
	public static HeuristicalEngine getOptimisticEngineForAStarEval(Mine mine, int targetLambda) {
		List<Heuristic> staticHeuristics = new ArrayList<Heuristic>();
	    
	    staticHeuristics.add(new RockOverLiftPenalty());
	    staticHeuristics.add(new ClosenessBonus(mine, targetLambda));
	    
	    if (mine.getCfg().isFloodingActive()) {
	    	staticHeuristics.add(new RobotUnderwaterPenalty());
	    } 
	    if (mine.getBeardState().getBeardCount() > 0) {
	    	staticHeuristics.add(new RazorIncreaseBonus());
	    }
	    
		return new HeuristicalEngine(mine, Collections.<Heuristic>emptyList(), staticHeuristics);
	}

}