package modelng;

import static org.junit.Assert.*;
import model.BeardState;
import model.Cell;
import model.ElementsConfig;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;

import org.junit.Test;

public class BeardsTest {
	@Test
	public void testParsingMap1() {
		Mine mine = MineFactory.getMineFromResource("/beard1.map.txt");
		assertEquals(mine.index(4, 2), mine.findCell(Cell.BEARD));
		assertEquals(mine.index(1, 5), mine.findCell(Cell.RAZOR));
		assertEquals(15, mine.getCfg().growthRate);
		assertEquals(0, mine.getCfg().initialRazors);
    }
	
	@Test
	public void testParsingMap2() {
		Mine mine = MineFactory.getMineFromResource("/beard2.map.txt");
		assertEquals(25, mine.getCfg().growthRate);
		assertEquals(10, mine.getCfg().initialRazors);
		assertEquals(10, mine.getRobot().getRazorCount());
	}
	
	@Test
	public void testParsingNonBeardMap() {
		Mine mine = MineFactory.getMineFromResource("/contest1.map.txt");
		assertSame(BeardState.NULL, mine.getBeardState());
	}
	
	@Test
	public void testRazorCollect() {
		Mine mine = MineFactory.getMineFromResource("/beard1.map.txt");
		mine = mine.makeMoves(Path.fromString("RDLL"));
		assertTrue(mine.getPossibleRobotMoves().contains(Move.D));
		assertEquals(0, mine.getRobot().getRazorCount());
		mine = mine.makeMove(Move.D);
		assertEquals(1, mine.getRobot().getRazorCount());
	}
	
	@Test
	public void testBeardGrowth() {
		ElementsConfig elementsConfig = new ElementsConfig(2, 0);
		String spec = " . \n" +
					  " W.\n" +
					  "###";
		Mine mine = MineFactory.getMine(spec, elementsConfig);
		assertEquals(spec, mine.toString());
		assertEquals(1, mine.getBeardState().getBeardCount());
		assertEquals(spec, mine.makeMove(Move.W).toString());
		
		Mine newMine = mine.makeMove(Move.W).makeMove(Move.W);
		assertEquals("W.W\nWW.\n###", newMine.toString());
		assertEquals(4, newMine.getBeardState().getBeardCount());
	}
	
	@Test
	public void testShavingPossible() {
		Mine mine = MineFactory.getMineFromResource("/beard1.map.txt");
		assertFalse(mine.getPossibleRobotMoves().contains(Move.S));
		Mine shaveable = mine.makeMoves(Path.fromString("RRLDLLDURRRDDD"));
		assertTrue(shaveable.getPossibleRobotMoves().contains(Move.S));
	}
	
	
	@Test
	public void testShaveAction() {
		ElementsConfig elementsConfig = new ElementsConfig(2, 2);
		String spec = "W. W\n" +
					  "WRWW\n" +
					  "### ";
		Mine mine = MineFactory.getMine(spec, elementsConfig);
		assertEquals(5, mine.getBeardState().getBeardCount());
		assertEquals(spec, mine.makeMove(Move.W).toString());
		assertEquals(" . W\n R W\n### ", mine.makeMove(Move.S).toString());
		assertEquals(2, mine.makeMove(Move.S).getBeardState().getBeardCount());
	}

}
