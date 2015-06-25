package algo.simple.lookahead;

import algo.simple.heuristics.Delta;
import model.Move;

public class LookaheadEvaluatedMove {
	private final Move move;
	private final Delta currentHeuristicalScore;
	private final int remainingDepth;

	public LookaheadEvaluatedMove(Move lastMove, Delta score, int remainingDepth) {
		this.move = lastMove;
		this.currentHeuristicalScore = score;
		this.remainingDepth = remainingDepth;
	}

	public Delta getScore() {
		return currentHeuristicalScore;
	}

	public int getRemainingDepth() {
		return remainingDepth;
	}

	@Override
	public String toString() {
		return move + "=" + currentHeuristicalScore + " (" + remainingDepth + ")";
	}

	public Move getMove() {
		return move;
	}
}