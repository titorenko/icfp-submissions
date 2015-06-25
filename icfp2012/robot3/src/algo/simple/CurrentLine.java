package algo.simple;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import model.Mine;
import model.Move;
import model.Path;
import algo.simple.Evaluator.ScoredPosition;
import algo.simple.lookahead.LookaheadEvaluatedMove;
import algo.simple.lookahead.LookaheadMoveSequence;

public class CurrentLine {
	private final LinkedList<ScoredPosition> currentLineMoves = new LinkedList<ScoredPosition>(); 
	private final Deque<Mine> currentLineMines = new LinkedList<Mine>();
	private final Evaluator evaluator;

	public CurrentLine(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public void add(Mine mine) {
		mine = new MapSimplifier(mine).wipeEarth();
		currentLineMines.add(mine);
		List<LookaheadMoveSequence> movesToPlay = evaluator.findBestMovesThrottled(mine);
		ScoredPosition position = evaluator.new ScoredPosition(movesToPlay);
		/*System.out.println(mine);
		System.out.println("\n"+currentPath());
		int idx = 0;
		for (LookaheadMoveSequence m : movesToPlay) {
			System.out.println(idx+") "+m);
			idx++;
		}*/
		currentLineMoves.add(position);
	}

	public LookaheadEvaluatedMove nextMove() {
		while (currentLineMoves.size() > 0) {
			ScoredPosition position = currentPosition();
			LookaheadEvaluatedMove move = position.nextMove();
			if (move == null) {
				backTrack();
			} else {
				return move;
			}
		}
		return null;
	}
	
	void backTrack() {
		if (currentLineMoves.size() == 0) return;
		currentLineMoves.removeLast();
		currentLineMines.removeLast();
	}
	
	public ScoredPosition currentPosition() {
		return currentLineMoves.peekLast();
	}

	public boolean isDeadEnd() {
		ScoredPosition currentPosition = currentPosition();
		if (currentPosition.hasNoMoves()) return true;
		return currentPosition.getScore().isDeadEnd();
	}

	public Mine currentMine() {
		return currentLineMines.peekLast();
	}

	public Path currentPath() {
		List<Move> moves = new ArrayList<Move>();
		for (ScoredPosition p : currentLineMoves) {
			LookaheadEvaluatedMove currentMove = p.currentMove();
			if (currentMove!= null) moves.add(currentMove.getMove());
		}
		return new Path(moves);
	}


	public ScoredPosition getPosition(int index) {
		return currentLineMoves.get(index);
	}
}
