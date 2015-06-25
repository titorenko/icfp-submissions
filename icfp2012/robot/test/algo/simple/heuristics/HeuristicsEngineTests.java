package algo.simple.heuristics;

import static org.junit.Assert.assertTrue;

import java.util.List;

import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;

import org.junit.Ignore;
import org.junit.Test;

import algo.simple.Evaluator;
import algo.simple.EvaluatorFactory;
import algo.simple.lookahead.LookaheadMoveSequence;

public class HeuristicsEngineTests {

	private static String spec = 
			"#######################################\n"
			+ "#    ................#..1...\\\\\\\\\\\\\\B..#\n" 
			+ "#   *....##############################\n"
			+ "#   ....    ..........................#\n" 
			+ "#   ....           \\            ......#\n"
			+ "#   .R.       . ...#....... ..........#\n" 
			+ "#         .     ...#.A..... ..........#\n"
			+ "#        ..     ...#.......    *  \\\\..#\n" 
			+ "#        ..     ...#....... ..........#\n"
			+ "#              *...#....... ..........#\n" 
			+ "#*       .. ** ....#................**#\n"
			+ "#.. ***  .. .......#................\\\\#\n" 
			+ "########### ############## ############\n"
			+ "#...*.................................#\n" 
			+ "#....*..................        ......#\n"
			+ "#... .*....*.............. ..... .....#\n" 
			+ "#....*2*........########.. ..... .....L\n"
			+ "#...*...*.......#\\\\\\#..... ...*.......#\n" 
			+ "#.....\\\\\\.......#\\\\\\#....**..***......#\n"
			+ "#....    .......#\\\\\\#*................#\n" 
			+ "#...............#\\\\\\#*...**...*.......#\n"
			+ "#...............#.....................#\n" 
			+ "######       ############## ### #######\n"
			+ "#\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\#\n"
			+ "#######################################";

	@Test
	public void testLambdaCloseness() {
		Mine game = MineFactory.getMine(spec);
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		int scoreR = e.getCtx().tesHeuristicsValue(game.makeMove(Move.R));
		int scoreL = e.getCtx().tesHeuristicsValue(game.makeMove(Move.L));
		assertTrue(scoreR > scoreL);
	}

	@Test
	public void testLiftPenalty() {
		Mine game = MineFactory.getMineFromResource("/contest2.map.txt");
		game = game.makeMove(Move.R).makeMove(Move.R);
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		int scoreU = e.getCtx().tesHeuristicsValue(game.makeMove(Move.U));
		int scoreL = e.getCtx().tesHeuristicsValue(game.makeMove(Move.L));
		assertTrue(scoreL < -100);//leads to close lift
		assertTrue(scoreU > scoreL);//ok
	}

	
	@Test
	@Ignore
	public void testBlockLift() {
		Mine game = MineFactory.getMineFromResource("/trampoline1.map.txt");
		game = game.makeMove(Move.D).makeMove(Move.L);
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		int scoreL = e.getCtx().tesHeuristicsValue(game.makeMove(Move.L));
		int scoreR = e.getCtx().tesHeuristicsValue(game.makeMove(Move.R));
		assertTrue(scoreL<-100);
		assertTrue(scoreR>-15);
	}
	
	@Test
	@Ignore
	public void testBlockLift2() {
		Mine game = MineFactory.getMineFromResource("/trampoline1.map.txt");
		game = game.makeMove(Move.D).makeMove(Move.L);
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		List<LookaheadMoveSequence> moves = e.findBestMoves(game);
		System.out.println(moves);
	}
	
	@Test
	public void testUnblocksLift() {
		Mine game = MineFactory.getMineFromResource("/contest8.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		
		game = game.makeMoves(Path.fromString("UUUUUUDDU"));
		int scoreL = e.getCtx().tesHeuristicsValue(game.makeMove(Move.W));
		assertTrue(scoreL < -500);
	}
	
	@Test
	public void testDeathPeanlty() {
		Mine game = MineFactory.getMineFromResource("/contest8.map.txt");
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(game);
		
		game = game.makeMoves(Path.fromString("UUUUUUDDU"));
		int scoreL = e.getCtx().tesHeuristicsValue(game.makeMove(Move.D));
		assertTrue(scoreL < -500);
	}
	

	/*public static void main(String[] args) {
		Mine mine = MineFactory.getMineNgFromResource("/contest2.map.txt");
		//mine = mine.makeMoves(Path.fromString("UUUR"));
		Evaluator e = EvaluatorFactory.getDefaultEvaluator(mine);
		List<LookaheadMoveSequence> bestMoves = e.findBestMoves(mine);

		System.out.println(bestMoves);
	}*/
}
