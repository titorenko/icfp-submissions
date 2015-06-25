package modelng;

import static org.junit.Assert.assertEquals;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Move;

import org.junit.Test;

public class MineUpdateTest {
	
	@Test
	public void testOneCellMineStaysSame() {
		Mine mine = new Mine(1, Cell.ROCK);
		assertEquals("*", mine.toString());
		assertEquals("*", mine.makeMove(Move.W).toString());
		assertEquals(0, mine.getMovingObstacles().size());
	}
	
	@Test
	public void testRockFallsDown() {
		Mine mine = new Mine(1, Cell.EMPTY, Cell.ROCK);
		assertEquals("*\n ", mine.toString());
		assertEquals(" \n*", mine.makeMove(Move.W).toString());
		assertEquals(1, mine.makeMove(Move.W).getMovingObstacles().size());
		assertEquals(0, (int) mine.makeMove(Move.W).getMovingObstacles().get(0));
	}
	
	@Test
	public void testRockHeldByRobot() {
		Mine mine = new Mine(1, Cell.EMPTY, Cell.ROBOT, Cell.ROCK);
		assertEquals("*\nR\n ", mine.toString());
		assertEquals(mine.toString(), mine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testMoveRock() {
		Mine mine = new Mine(3, Cell.ROBOT, Cell.ROCK, Cell.EMPTY);
		Mine newMine = mine.makeMove(Move.R);
		assertEquals(" R*", newMine.toString());
		assertEquals(1, newMine.getMovingObstacles().size());
		assertEquals(2, (int)newMine.getMovingObstacles().get(0));
	}
	
	@Test
	public void testRockNotHeldByRobotInNoRobotMine() {
		Mine mine = new Mine(1, Cell.EMPTY, Cell.ROBOT, Cell.ROCK);
		Mine removedRobotMine = mine.removeRobot();
		assertEquals("*\n \n ", removedRobotMine.toString());
		assertEquals(" \n*\n ", removedRobotMine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testRockFallsDownJustOnce() {
		Mine mine = new Mine(1, Cell.EMPTY, Cell.EMPTY, Cell.ROCK);
		assertEquals("*\n \n ", mine.toString());
		assertEquals(" \n*\n ", mine.makeMove(Move.W).toString());
		assertEquals(" \n \n*", mine.makeMove(Move.W).makeMove(Move.W).toString());
	}
	
	/**
	 *          *E        EE
	 * 			*E   -->  ** 
	 * 
	 * (rule 3)
	 */
	@Test
	public void testRockFallsRight() {
		Mine mine = new Mine(2, Cell.ROCK, Cell.EMPTY, Cell.ROCK, Cell.EMPTY);
		assertEquals("* \n* ", mine.toString());
		assertEquals("  \n**", mine.makeMove(Move.W).toString());
	}
	
	/**
	 *          E*#        EE#
	 * 			E*E   -->  **E 
	 * 
	 * (rule 4a)
	 */
	@Test
	public void testRockFallsLeftIfBlockedToTheRight1() {
		Mine mine = new Mine(3, Cell.EMPTY, Cell.ROCK, Cell.EMPTY, Cell.EMPTY, Cell.ROCK, Cell.WALL);
		assertEquals(" *#\n * ", mine.toString());
		assertEquals("  #\n** ", mine.makeMove(Move.W).toString());
	}
	
	/**
	 *          E*E        EEE
	 * 			E*#   -->  **# 
	 * 
	 * (rule 4b)
	 */
	@Test
	public void testRockFallsLeftIfBlockedToTheRight2() {
		Mine mine = new Mine(3, Cell.EMPTY, Cell.ROCK, Cell.WALL, Cell.EMPTY, Cell.ROCK, Cell.EMPTY);
		assertEquals(" * \n *#", mine.toString());
		assertEquals("   \n**#", mine.makeMove(Move.W).toString());
	}
	
	/**
	 *          *E        EE
	 * 			\\E   -->  \\* 
	 * 
	 * (rule 5)
	 */
	@Test
	public void testRockFallsOnTheLambdaToTheRight() {
		Mine mine = new Mine(2, Cell.LAMBDA, Cell.EMPTY, Cell.ROCK, Cell.EMPTY);
		assertEquals("* \n\\ ", mine.toString());
		assertEquals("  \n\\*", mine.makeMove(Move.W).toString());
	}
	
	
	/**
	 *          E*        E*
	 * 			E\\   --> E\\ 
	 * 
	 * (rule 5)
	 */
	@Test
	public void testRockDoesnotFallsOnTheLambdaToTheLeft() {
		Mine mine = new Mine(2, Cell.EMPTY, Cell.LAMBDA, Cell.EMPTY, Cell.ROCK);
		assertEquals(" *\n \\", mine.toString());
		assertEquals(" *\n \\", mine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testLiftOpens() {
		Mine mine = new Mine(1, Cell.CLOSED_LIFT);
		assertEquals("L", mine.toString());
		assertEquals("O", mine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testLiftDoesnotOpenIfLambdaPresent() {
		Mine mine = new Mine(2, Cell.CLOSED_LIFT, Cell.LAMBDA);
		assertEquals("L\\", mine.toString());
		assertEquals("L\\", mine.makeMove(Move.W).toString());
	}

	/**
	 * 
			#*E*#			 #EEE#
			#*E*# ------>	 #***#
			##### 			 #####
	 */
	@Test
	public void testExampleFromPDF() {
		Mine mine = new Mine(5, Cell.WALL, Cell.WALL, Cell.WALL, Cell.WALL, Cell.WALL,
				Cell.WALL, Cell.ROCK, Cell.EMPTY, Cell.ROCK, Cell.WALL,
				Cell.WALL, Cell.ROCK, Cell.EMPTY, Cell.ROCK, Cell.WALL);
		assertEquals("#   #\n#***#\n#####", mine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testNullPointerRegression() {
		String mineSpec = 
				"######"+"\n"+
				"L  .\\#"+"\n"+
				"#\\ R #"+"\n"+
				"# *\\ #"+"\n"+
				"#.*  #"+"\n"+
				"######";
		Mine mine = MineFactory.getMine(mineSpec);
		assertEquals(mineSpec, mine.toString());
		Mine updated = mine.makeMove(Move.W);
		assertEquals(mine.toString(), updated.toString());
	}
}