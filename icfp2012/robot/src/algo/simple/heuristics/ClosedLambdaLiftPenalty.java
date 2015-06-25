package algo.simple.heuristics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import model.Cell;
import model.Mine;
import algo.simple.lookahead.HeuristicEngineContext;

public class ClosedLambdaLiftPenalty implements Heuristic {
	private final BatchedClosedCellEvaluator evaluator;
	
	public ClosedLambdaLiftPenalty(Mine game) {
		evaluator = new BatchedClosedCellEvaluator(game);
	}

	@Override
	public Delta getDelta(Mine mine, HeuristicEngineContext ctx) {
		List<Integer> cells = new ArrayList<Integer>(mine.getLambdaCells());
		cells.add(mine.getLiftCell());
		ClosedSetEvalResult result =  evaluator.findClosed(mine, ctx, cells);
		BitSet closedCells = result.closed;
		BitSet restrictedCells = result.restricted;
		boolean isStrictlyClosed = false;
		int score = 0;
		int bound = Math.max(closedCells.length(), restrictedCells.length());
		for (int i = 0; i < bound; i++) {
			if (closedCells.get(i)) {
				isStrictlyClosed = true;
				Cell cell = mine.unsafeGet(i);
				if (cell == Cell.LAMBDA) {
					score -= 50;
				} else {
					score -= 1;
				} 
			} else if (restrictedCells.get(i)) {
				score -= 1;
			}
		}
		if (score == 0) return Delta.NULL;
		score -= mine.getInitialAllLambdaCount() * 25;
		Delta delta = new Delta(score, isStrictlyClosed);
		//System.out.println("d= "+delta+" for "+mine);
		return delta;
	}
}