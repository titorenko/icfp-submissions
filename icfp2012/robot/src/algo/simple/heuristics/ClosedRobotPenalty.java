package algo.simple.heuristics;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Mine;
import model.Move;
import model.Path;
import algo.simple.lookahead.HeuristicEngineContext;

public class ClosedRobotPenalty implements Heuristic {
	private final static int CACHE_SIZE = 400000;
	
	private final int minCells;
	
	private final BitSet current;
	private int currentSize = 0;
	
	private Map<Path, Boolean> cache = new LinkedHashMap<Path, Boolean>(CACHE_SIZE) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Path,Boolean> eldest) {
			return size() > CACHE_SIZE; 
		}
	};
	
	
	public ClosedRobotPenalty(Mine mine) {
		this.minCells = mine.getWidth() + mine.getHeight() - 2;
		this.current = new BitSet(mine.getWidth()*mine.getHeight());
	}

	public boolean findClosed(Mine mine) {
		Path path = mine.getRobot().getPath();
		if (mine.getMovingObstacles().size() == 0 && !mine.getRobot().hasJumped()) {
			Boolean isClosed = cache.get(path.removeLastMove());
			if (isClosed != null) {
				cache.put(path, isClosed);
				return isClosed;
			}
		}
		reset();
		boolean result = !isOpenArea(mine);
		cache.put(path, result);
		return result;
	}

	private void reset() {
		current.clear();
		currentSize = 0;
	}
	
	private boolean isOpenArea(Mine mine) {
		int idx = mine.getRobotCell();
		boolean contains = current.get(idx);
		if (contains) return false;
		if (mine.getState().isDead()) return false;
		current.set(idx);
		currentSize++;
		if (currentSize > minCells) return true;
		final List<Move> possibleMoves = mine.getPossibleRobotMoves(); 

		boolean result = false;
		for (Move move : possibleMoves) {
			result = result || isOpenArea(mine.makeMove(move));
		}
		return result;
	}

	
	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		boolean isClosed = findClosed(mine);
		if (!isClosed) return Delta.NULL;
		int score = -(mine.getUncollectedLambdaCount()*75+mine.getRobot().getLambdaCollected()*25);
		return new Delta(score, true);
	}

}