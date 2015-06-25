package algo.simple;

import java.util.HashMap;
import java.util.Map;

import model.Mine;
import model.Path;
import model.RobotState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algo.simple.heuristics.Delta;
import algo.simple.heuristics.HeuristicalEngine;

public class EvaluationContext {

	private static final Logger logger = LoggerFactory.getLogger(EvaluationContext.class);

	static final int STRICTEST_PENALTY = -1000000;
	static final int ALREADY_VISITED_PENALTY = -10000;
	
	int initialDepth = 100000;

	Map<Integer, Integer> visitedPositions = new HashMap<Integer, Integer>(1000);
	Map<Integer, Integer> lookeadAheadVisitedPositions = new HashMap<Integer, Integer>(1000000);
	
	Path bestPath;
	int bestScore;

	Path bestPathLookAhead;
	int bestScoreLookAhead;
	
	boolean isBestScoreWinning = false;

	int lookAheadDepth;

	private long lastProgressTime = 0;
	private long lastSpeedChange = 0;
	private long evalStartTime;

	private HeuristicalEngine hEngine;

	private long lookaheadsEvaluated = 0;
	private int movesEvaluated = 0;

	private boolean isBestScoreFound;


	public EvaluationContext(HeuristicalEngine hEngine) {
		this.hEngine = hEngine;
		this.lookAheadDepth = initialDepth;
	}

	public Integer record(Mine mine) {
		movesEvaluated++;
		isBestScoreFound = false;
		Integer alreadyVisited = visitedPositions.put(mine.hashCode(), mine.getRobot().getPath().length());
		if (mine.getScore() > bestScore) {
			bestPath = mine.getRobot().getPath();
			bestScore = mine.getScore();
			lastProgressTime = System.currentTimeMillis();
			isBestScoreWinning = mine.getState() == RobotState.WINNING;
			isBestScoreFound = bestScore >= bestScoreLookAhead;
			logState();
		}
		return alreadyVisited;
	}
	
	public boolean isBestScoreFound() {
		return isBestScoreFound;
	}
	
	public HeuristicalEngine getHeuristicalEngine() {
		return hEngine;
	}

	public void recordLookAhead(Mine game) {
		lookaheadsEvaluated++;
		//isBestPathIncreasedRapidly = false;
		if (game.getScore() > bestScoreLookAhead) {
			bestPathLookAhead = game.getRobot().getPath();
			bestScoreLookAhead = game.getScore();
			//lastProgressTime = System.currentTimeMillis();
			logState();
		}
	}
	
	private void logState() {
		logger.info("score: "+bestScore+"/"+bestScoreLookAhead+", speed = "+getEvalSpeed()+" looked at "+lookaheadsEvaluated+": "+getAbsoluteBestPath());
	}

	private int getEvalSpeed() {
		return (int) ((lookaheadsEvaluated * 1000) / evalDuration());
	}

	public boolean isStuck() {
		return Math.abs(lastProgressTime - System.currentTimeMillis()) > (Evaluator.TIME_LIMIT_SEC * 800);
	}
	
	private int lastProgressTimeDistance() {
		 return (int) Math.abs(lastProgressTime - System.currentTimeMillis());
	}

	public Path getBestPath() {
		return bestPath;
	}

	public Path getAbsoluteBestPath() {
		return bestScore < bestScoreLookAhead ? bestPathLookAhead : bestPath;
	}

	public int getLookAheadDepth() {
		return lookAheadDepth;
	}
	
	public void throttle(Mine mine, int evalMovesTime) {
		if (Math.abs(System.currentTimeMillis() - lastSpeedChange) < Evaluator.TIME_LIMIT_SEC*1000 / 16) return;
		if (isBestScoreWinning) {
			finalSpurt();
			return;
		}
		int maxMoves = mine.getHeight()*mine.getWidth();
		int movesMade = bestPath == null ? 0 : bestPath.length();
		int desiredTimePerMove = getTimeRemaining() / Math.max(maxMoves-movesMade, 1);
		if (evalMovesTime > desiredTimePerMove*4 || lastProgressTimeDistance() > (1000 * Evaluator.TIME_LIMIT_SEC / 8)) {
			lastSpeedChange = System.currentTimeMillis();
			speedup();
		}
		
		if (evalMovesTime < desiredTimePerMove/4 && lastProgressTimeDistance() < (Evaluator.TIME_LIMIT_SEC *1000 / 16) ) {
			lastSpeedChange = System.currentTimeMillis();
			slowdown();
		}
		
	}

	private void finalSpurt() {//TODO: try without me
		/*if (lookAheadDepth > 256) {
			lookAheadDepth = 256;
			logger.info("final spurt");
		}*/
	}

	private void speedup() {
		if (lookAheadDepth < 100)
			return;
		lookAheadDepth = lookAheadDepth / 4;
		logger.info("speedup: "+lookAheadDepth);
	}

	private void slowdown() {
		if (lookAheadDepth > initialDepth)
			return;
		lookAheadDepth = lookAheadDepth * 4;
		logger.info("slowdown: "+lookAheadDepth);
	}

	public int getBestScore() {
		return bestScore;
	}

	void resetStuckness() {
		lastProgressTime = System.currentTimeMillis();
		evalStartTime = System.currentTimeMillis();
	}
	
	private int evalDuration() {
		int duration = (int) (System.currentTimeMillis() - evalStartTime);
		return duration == 0 ? 1 : duration;
	}
	
	int getTimeRemaining() {
		int remaining = Evaluator.TIME_LIMIT_SEC * 1000 - (int) (System.currentTimeMillis() - evalStartTime);
		return remaining <= 0 ? 1 : remaining;
	}
	
	@Override
	public String toString() {
		return "Score "+Math.max(bestScoreLookAhead, bestScore)+" with path "+getAbsoluteBestPath()+" moves eval "+movesEvaluated+" look ahead evals "+lookaheadsEvaluated;
	}

	public int tesHeuristicsValue(Mine mine) {
		return tesHeuristics(mine).getValue();
	}

	public Delta tesHeuristics(Mine mine) {
		return hEngine.applyHeuristics(mine);
	}
	
	public Map<Integer, Integer> getLookeadAheadVisitedPositions() {
		return lookeadAheadVisitedPositions;
	}

}
