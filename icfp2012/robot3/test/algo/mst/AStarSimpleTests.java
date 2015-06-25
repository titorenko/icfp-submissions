package algo.mst;

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
		
		AStarConnectionFinder pathFinder = new AStarConnectionFinder();
		Edge edge = pathFinder.findConnection(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = edge.getEndPath();
		assertTrue("RRRD".equals(path.toString()) || "DRRR".equals(path.toString()));
	}
	
	@Test
	public void test2() {
		Mine mine = MineFactory.getMine(
			"R    \n"+
			"   #O\n");
		AStarConnectionFinder pathFinder = new AStarConnectionFinder();
		Edge edge = pathFinder.findConnection(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = edge.getEndPath();
		assertTrue("RRRRD".equals(path.toString()) || "DRRURRD".equals(path.toString()));
	}
	
	@Test
	public void test3() {
		Mine mine = MineFactory.getMine(
			"     \n"+
			"R    \n"+
			" ### \n"+
			"##   \n"+
			"O    ");
		
		AStarConnectionFinder pathFinder = new AStarConnectionFinder();
		Edge edge = pathFinder.findConnection(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = edge.getEndPath();
		assertEquals("RRRRDDDLLLL", path.toString());
	}
	
	@Test
	public void test4() {
		Mine mine = MineFactory.getMine(
				"R** \n"+
				"#*  O");
		
		AStarConnectionFinder pathFinder = new AStarConnectionFinder();
		Edge edge = pathFinder.findConnection(mine, mine.findCell(Cell.OPEN_LIFT));
		Path path = edge.getEndPath();
		assertEquals("WRRRRD", path.toString());
	}
}
