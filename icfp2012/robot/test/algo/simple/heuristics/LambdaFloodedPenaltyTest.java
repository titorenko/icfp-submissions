package algo.simple.heuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;

import org.junit.Test;

import algo.simple.CurrentLine;
import algo.simple.Evaluator;
import algo.simple.EvaluatorFactory;
import algo.simple.lookahead.HeuristicEngineContext;

public class LambdaFloodedPenaltyTest {
	
	LambdaFloodedPenalty lambdaFloodedPenalty = new LambdaFloodedPenalty();
	
	
	@Test
	public void testFloodingLevelRisesContinuously() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		double level0 = lambdaFloodedPenalty.getFloodLevel(mine);
		assertEquals(1.0, level0, 1E-10);
		
		mine = mine.makeMove(Move.W);
		double level1 = lambdaFloodedPenalty.getFloodLevel(mine);
		assertEquals(1.1, level1, 1E-10);
		
		mine = mine.makeMove(Move.W);
		double level2 = lambdaFloodedPenalty.getFloodLevel(mine);
		assertEquals(1.2, level2, 1E-10);
	}
	
	@Test
	public void testInitialPenaltyOnMap3() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		Delta delta = lambdaFloodedPenalty.getDelta(mine, getLaCtx(mine));
		assertTrue("Expected large penalty due to 3 flooded deltas, but was: "+delta, delta.getValue() < -10);
	}
	
	@Test
	public void testPenaltyDecreasesWhenLambdaCollected() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		Delta delta = lambdaFloodedPenalty.getDelta(mine, getLaCtx(mine));
		Mine newMine = mine.makeMoves(Path.fromString("LLUURRRRDD"));
		Delta delta2 = lambdaFloodedPenalty.getDelta(newMine, getLaCtx(newMine));
		assertTrue(delta.isLessThan(delta2));
	}
	
	@Test
	public void testBacktrackingDoesnotKickIn() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		CurrentLine line = new CurrentLine(e);
		line.add(mine);
		line.add(mine.makeMove(Move.L));
		assertFalse(line.isDeadEnd());
	}
	
	@Test
	public void testScoring() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		int s1 = e.getCtx().tesHeuristicsValue(mine.makeMoves(Path.fromString("LLUUURUURR")));
		int s2 = e.getCtx().tesHeuristicsValue(mine.makeMoves(Path.fromString("LLUURRRRDD")));
		System.out.println(s1);
		System.out.println(s2);
	}
	
	@Test
	public void testCriticalMove() {
		Mine mine = MineFactory.getMineFromResource("/flood3.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		Mine newMine = mine.makeMoves(Path.fromString("LLUU"));
		System.out.println(e.findBestMoves(newMine));
	}

	private HeuristicEngineContext getLaCtx(Mine mine) {
		return new HeuristicEngineContext();
	}
}
