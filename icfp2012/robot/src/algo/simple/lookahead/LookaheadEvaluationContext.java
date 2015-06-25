package algo.simple.lookahead;

import model.Mine;
import model.Move;
import algo.simple.EvaluationContext;

public class LookaheadEvaluationContext {

	private final EvaluationContext mainCtx;

	private LookaheadMoveSequence path = new LookaheadMoveSequence(this);
	private LookaheadMoveSequence bestPath = null;

	private HeuristicEngineContext heuristicEngineContext;

	public LookaheadEvaluationContext(Mine initialMine, EvaluationContext mainCtx) {
		this.mainCtx = mainCtx;
		this.heuristicEngineContext = new HeuristicEngineContext();
		mainCtx.getHeuristicalEngine().applyDynamicHeuristics(initialMine, heuristicEngineContext);
		heuristicEngineContext.setFollowUpEvalMode();		
	}

	public void resetBestPath() {
		this.bestPath = null;
	}

	public LookaheadMoveSequence getBestPath() {
		return bestPath;
	}
	
	public HeuristicEngineContext getHeuristicEngineContext() {
		return heuristicEngineContext;
	}

	public boolean visitNewMine(Mine mine, Move lastMove, int nCandidatesConsidered) {
		mainCtx.recordLookAhead(mine);
		if (mine.getState().isDead()) {
			path.onDeath(lastMove);
		} else if (mine.getState().isFinished()) {
			path.onFinish(mine, lastMove);
		} else {
			path.onNormalMove(mine, lastMove, nCandidatesConsidered);
		}
		if ( path.isFinished() && path.isBetter(bestPath)) {
			bestPath = path.clone();
		}
		int hash = mine.hashCode();
	/*	if (mainCtx.getVisitedPositions().containsKey(hash))
			return true;*/
		Integer depth = mainCtx.getLookeadAheadVisitedPositions().get(hash);
		if (depth != null && depth < path.size() + 1) {
			return true;
		}
		mainCtx.getLookeadAheadVisitedPositions().put(hash, path.size());
		return path.isFinished();
	}

	public void pop() {
		path.pop();

	}

	@Override
	public String toString() {
		return bestPath + " / " + path + " with hc " + heuristicEngineContext;
	}

	public EvaluationContext getMainCtx() {
		return mainCtx;
	}

}