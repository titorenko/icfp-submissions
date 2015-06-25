package modelng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import model.Mine;
import model.MineFactory;
import model.Move;

import org.junit.Before;
import org.junit.Test;

public class PossibleMovesTest {
	private Mine mine;

	@Before
	public void init() {
		String spec = "############\n"+
		"#..........#\n"+
		"#..... ....#\n"+
		"#..    *  .#\n"+
		"#. W   .. .#\n"+
		"#..       .#\n"+
		"#.. ..    .#\n"+
		"#.  .. ....#\n"+
		"#.     ..* #\n"+
		"#. ### ### #\n"+
		"#.   #R#\\.#\n"+
		"######L#####";
		this.mine = MineFactory.getMine(spec);
	}
	
	@Test
	public void testPossibleMoves() {
		List<Move> possibleRobotMoves = mine.getPossibleRobotMoves();
		assertEquals("moves: "+possibleRobotMoves, 2, possibleRobotMoves.size());
		assertTrue(possibleRobotMoves.contains(Move.U));
		assertTrue(possibleRobotMoves.contains(Move.W));
	}
}