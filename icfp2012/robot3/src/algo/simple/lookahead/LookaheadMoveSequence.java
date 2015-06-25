package algo.simple.lookahead;

import java.util.Deque;
import java.util.LinkedList;

import model.Mine;
import model.Move;
import algo.simple.heuristics.Delta;

public class LookaheadMoveSequence implements Comparable<LookaheadMoveSequence> {
	private Deque<LookaheadEvaluatedMove> pathUnderConsideration = new LinkedList<LookaheadEvaluatedMove>();
	private final LookaheadEvaluationContext ctx;
	
	LookaheadMoveSequence(LookaheadEvaluationContext ctx) {
		this.ctx = ctx;
	}

	public void onNormalMove(Mine mine, Move lastMove, int nCandidatesConsidered) {
		LookaheadEvaluatedMove previous = pathUnderConsideration.peekLast();
		int remainingDepth = previous == null ? ctx.getMainCtx().getLookAheadDepth() : previous.getRemainingDepth();
		Delta score = scoreMine(mine);
		LookaheadEvaluatedMove move = new LookaheadEvaluatedMove(lastMove, score, remainingDepth / nCandidatesConsidered);
		pathUnderConsideration.add(move);
	}
	
	public void onFinish(Mine mine, Move lastMove) {
		pathUnderConsideration.add(new LookaheadEvaluatedMove(lastMove, scoreMine(mine), 0));

	}

	public void onDeath(Move lastMove) {
		pathUnderConsideration.add(new LookaheadEvaluatedMove(lastMove, Delta.DEATH, 0));
	}

	public int size() {
		return pathUnderConsideration.size();
	}

	public void pop() {
		pathUnderConsideration.pollLast();

	}

	public LookaheadEvaluatedMove getFirstMove() {
		return pathUnderConsideration.peekFirst();
	}

	public LookaheadEvaluatedMove getLastMove() {
		return pathUnderConsideration.peekLast();
	}

	public boolean isFinished() {
		LookaheadEvaluatedMove last = getLastMove();
		return last != null && last.getRemainingDepth() < 1;
	}
	
	public Delta getEndScore() {
		return getLastMove().getScore();
	}

	public Delta scoreMine(Mine game) {
		return ctx.getMainCtx().getHeuristicalEngine().applyHeuristics(game, ctx.getHeuristicEngineContext());
	}

	boolean isBetter(LookaheadMoveSequence other) {
		return compareTo(other) < 0;
	}

	@Override
	protected LookaheadMoveSequence clone() {
		LookaheadMoveSequence clone = new LookaheadMoveSequence(ctx);
		clone.pathUnderConsideration = new LinkedList<LookaheadEvaluatedMove>(
				pathUnderConsideration);
		return clone;
	}

	@Override
	public int compareTo(LookaheadMoveSequence other) {
		if (other == null)
			return -1;
		int otherSize = other.pathUnderConsideration.size();
		int thisSize = pathUnderConsideration.size();
		int scoreDiff = other.getLastMove().getScore().compareTo(getLastMove().getScore());
		return scoreDiff + (otherSize - thisSize);
	}

	@Override
	public String toString() {
		StringBuffer path = new StringBuffer(); 
		for (LookaheadEvaluatedMove move: pathUnderConsideration) {
			path.append(move.getMove().toString());
		}
		return path + " "+getEndScore();
	}

}