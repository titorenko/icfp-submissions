package algo.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import model.Mine;
import model.MineFactory;
import model.Path;
import model.RobotState;

import org.junit.Test;

import algo.simple.heuristics.Delta;
import algo.simple.lookahead.LookaheadMoveSequence;

public class EvaluatorBestMoveTest1 {
	
	@Test
	public void testCanSolveMapOne() {
		Mine game = MineFactory.getMineFromResource("/contest1.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		game = game.makeMoves(Path.fromString("DULR"));
		Path path = e.getPath(game);
		assertEquals(RobotState.WINNING, game.makeMoves(path).getState());
	}
	
	@Test 
	public void testCanMakeDecisionOnMap8() {
		Mine mine = MineFactory.getMineFromResource("/contest8.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		e.getCtx().lookAheadDepth = 2000;
		mine = mine.makeMoves(Path.fromString("UUUUU"));
		List<LookaheadMoveSequence> bestMoves = e.findBestMoves(mine);
		LookaheadMoveSequence lookaheadMoveSequence = bestMoves.get(0);
		System.out.println(lookaheadMoveSequence.getFirstMove().getScore());
		assertTrue(lookaheadMoveSequence.getFirstMove().getScore().getValue() < -500);
		assertTrue(lookaheadMoveSequence.getLastMove().getScore().getValue() > -50);
	}
	
	
	/*@Test
	public void testDoNotGoBackOnMap6() {
		Mine mine = MineFactory.getMineFromResource("/contest6.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		mine = mine.makeMoves(Path.fromString("RUULRRRRRRRRRRUUULLLLDLLLLLLUUUUUUUURRRDDRRRDDLLLLLLUUURDLDDDDDDRRRURRRRRRRRRDD"));
		//System.out.println(e.getPath(mine));
		//*System.out.println(mine);
		List<LookaheadMoveSequence> bestMoves = e.findBestMoves(mine);
		System.out.println(bestMoves);
	}*/
	
	@Test
	public void testWhenBeardBlocksSomethingButRazorsPresentThenWeCanStillContinue() {
		Mine mine = MineFactory.getMineFromResource("/horock3.map.txt", "URRUULLUUUUURUUURRRDDDRLR");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		Delta delta = e.getCtx().tesHeuristics(mine);
		assertFalse(delta.isDeadEnd());
	}
}