package algo.simple.heuristics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import model.Cell;
import model.Mine;
import model.Move;
import algo.simple.lookahead.HeuristicEngineContext;

public class BatchedClosedCellEvaluator {

	private final int minCells;
	private final int mineWidth;
	
	private Mine mine;

	private final BitSet current;
	private final BitSet closed;
	private final BitSet restricted;
	private final BitSet open;
	private int currentSize = 0;
	
	
	public BatchedClosedCellEvaluator(Mine mine) {
		this.minCells = mine.getWidth() + mine.getHeight() - 2;
		this.mineWidth = mine.getWidth();
		this.current = new BitSet(mine.getWidth()*mine.getHeight());
		this.closed = new BitSet(mine.getWidth()*mine.getHeight());
		this.restricted = new BitSet(mine.getWidth()*mine.getHeight());
		this.open = new BitSet(mine.getWidth()*mine.getHeight());
	}

	public ClosedSetEvalResult findClosed(Mine mine, HeuristicEngineContext ctx, Collection<Integer> cellIndexes) {
		reset(mine);
		
		for (Integer idx : cellIndexes) {
			boolean isClosedSquare = ctx.isClosedSquare(idx);
			boolean needsEval = isClosedSquare || ctx.isInitialEvalutation();
			int x = idx % mineWidth;
			int y = idx / mineWidth;
			if (needsEval || needsEvaluation(x, y)) {
				if (isClosed(idx)) {
					ctx.foundClosedSquare(idx);
				}
			}
		}
		return new ClosedSetEvalResult(closed, restricted);
	}

	void reset(Mine mine) {
		this.mine = mine;
		restricted.clear();
		closed.clear();
		open.clear();
	}
	
	void resetCurrent() {
		current.clear();
		currentSize = 0;
	}
	
	private boolean needsEvaluation(int cellX, int cellY) {
		if (mine.getRobot().distanceTo(cellX, cellX) < 3) return true;
		List<Integer> fallingRocks = mine.getMovingObstacles(); 
		
		for (Integer rock : fallingRocks) {
			int rx = rock % mineWidth;
			int ry = rock / mineWidth;
			if (Math.abs(cellX - rx) < 2 || Math.abs(cellY - ry) < 2) {
				return true;
			}
		}
		return false;
	}

	boolean isClosed(Integer idx) {
		if (closed.get(idx)) return true;
		if (restricted.get(idx)) return true;
		if (open.get(idx)) return false;
		boolean isRestricted = isRestrictedArea(idx);
		if (isRestricted) {
			BitSet currentRestricted = (BitSet) current.clone();
			if (isClosedArea(idx)) {
				closed.or(current);
			} else {
				restricted.or(currentRestricted);
			}
		} else {
			open.or(current);
		}
		return isRestricted;
	}
	
	
	boolean isClosedArea(int idx) {
		resetCurrent();
		return !isOpenArea(idx, true);
	}
	
	boolean isRestrictedArea(int idx) {
		resetCurrent();
		return !isOpenArea(idx, false);
	}

	private boolean isOpenArea(int idx, boolean isStrictMode) {
		if (mine.getRobotCell() == idx) 
			return true;//these cases will be handled by different penalty
		Cell cell = mine.unsafeGet(idx);
		if (cell.isTarget() || cell.isTrampoline()) {
			return true;
		}
		boolean contains = current.get(idx);
		current.set(idx);
		if (contains) return false;
		currentSize++;
		if (currentSize > minCells) return true;
	
		final int x = idx % mineWidth;
		final int y = idx / mineWidth;
		final List<Move> possibleMoves = getPossibleMovesForBottleneckEvaluation(x, y, isStrictMode);

		boolean result = false;
		for (Move move : possibleMoves) {
			int newIndexToCheck = mine.index(move.newX(x), move.newY(y));
			result = result || isOpenArea(newIndexToCheck, isStrictMode);
		}
		return result;
	}
	
	private List<Move> getPossibleMovesForBottleneckEvaluation(int x, int y, boolean isStrictMode) { 
    	List<Move> moves = new ArrayList<Move>(4);
    	if (isPossible(x, y+1, Move.U, isStrictMode))  {
			moves.add(Move.U);
		}
		if (isPossible(x, y-1, Move.D, isStrictMode)) {
			moves.add(Move.D);
		}
		if (isPossible(x-1, y, Move.L, isStrictMode)) {
			moves.add(Move.L);
		}
		if (isPossible(x+1, y, Move.R, isStrictMode)) {
			moves.add(Move.R);
		}
		return moves;
	}

	boolean isPossible(int x,int y, Move move, boolean isStrictMode) {
		Cell cell = mine.get(x, y);
		if (cell.canEasilyMoveInto() || cell.isTarget() || cell == Cell.ROBOT) return true;
		if (isStrictMode && cell == Cell.BEARD && (mine.getRobot().getRazorCount() > 0 || mine.getCellCount(Cell.RAZOR) > 0)) return true;
		if (isStrictMode && cell.isHardFallingThing()) {
			switch (move) {
			case L:
			case R:
				if (canBeMovedInto(x, y-1)) return true;
				break;
			case U:
				if (canBeMovedInto(x-1, y) && canBeMovedInto(x+1, y)) return true;
				break;
			case D:
				if (canBeMovedInto(x, y-1) || (canBeMovedInto(x-1, y) && canBeMovedInto(x+1, y))) return true;
				break;
			default:
				return false;
			}
		}
		return false;
	}

	private boolean canBeMovedInto(int x, int y) {
		while (y >= 0) {
			Cell cell = mine.get(x, y);
			if (cell == Cell.WALL || cell == Cell.NULL) return false;
			if (cell.canEasilyMoveInto()) {
				return true;
			}
			y--;
		}
		return false;
	}
}

class ClosedSetEvalResult {
	BitSet closed;
	BitSet restricted;

	public ClosedSetEvalResult(BitSet closed, BitSet restricted) {
		this.closed = closed;
		this.restricted = restricted;
	}
	
	public BitSet getClosed() {
		return closed;
	}
	
	public BitSet getRestricted() {
		return restricted;
	}
}
