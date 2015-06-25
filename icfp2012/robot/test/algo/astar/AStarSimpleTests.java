package algo.astar;

import static org.junit.Assert.*;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Test;

public class AStarSimpleTests {
	@Test
	public void test1() {
		Mine mine = MineFactory.getMine(
			"R...\n"+
			"...O\n");
		
		AStar pathFinder = new AStar(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = pathFinder.findPathToSquare();
		assertTrue("RRRD".equals(path.toString()) || "DRRR".equals(path.toString()));
	}
	
	@Test
	public void test2() {
		Mine mine = MineFactory.getMine(
			"R    \n"+
			"   #O\n");
		
		AStar pathFinder = new AStar(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = pathFinder.findPathToSquare();
		assertEquals("RRRRD", path.toString());
	}
	
	@Test
	public void test3() {
		Mine mine = MineFactory.getMine(
			"     \n"+
			"R    \n"+
			" ### \n"+
			"##   \n"+
			"O    ");
		
		AStar pathFinder = new AStar(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = pathFinder.findPathToSquare();
		assertEquals("RRRRDDDLLLL", path.toString());
	}
	
	@Test
	public void test4() {
		Mine mine = MineFactory.getMine(
				"R** \n"+
				"#*  O");
		
		AStar pathFinder = new AStar(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = pathFinder.findPathToSquare();
		assertEquals("WRRRRD", path.toString());
	}
}
