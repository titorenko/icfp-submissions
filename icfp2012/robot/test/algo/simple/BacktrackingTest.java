package algo.simple;

import static org.junit.Assert.*;
import model.Mine;
import model.MineFactory;

import org.junit.Test;

import algo.simple.heuristics.Delta;

public class BacktrackingTest {
	
	@Test
	public void testNoExcessiveBacktrackingOnMap9() {
		Mine mine = MineFactory.getMineFromResource("/contest9.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		Delta delta = e.getCtx().tesHeuristics(mine);
		assertFalse(delta.isDeadEnd());
	}
}
