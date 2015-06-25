package modelng;

import static org.junit.Assert.assertEquals;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.RobotState;

import org.junit.Test;

public class RobotMoveTest {
	@Test
	public void testMoveToEmpty() {
		Mine mine = MineFactory.getMine(" R ");
		assertEquals("R  ", mine.makeMove(Move.L).toString());
		assertEquals("  R", mine.makeMove(Move.R).toString());
		assertEquals(" R ", mine.makeMove(Move.U).toString());
		assertEquals(" R ", mine.makeMove(Move.D).toString());
		assertEquals(" R ", mine.makeMove(Move.W).toString());
	}
	
	@Test
	public void testMoveToEmptyRobotScore() {
		Mine mine = MineFactory.getMine(" R ");
		assertEquals(0, mine.getScore());
		assertEquals(-1, mine.makeMove(Move.L).getScore());
	}
	
	@Test
	public void testMoveToLambda() {
		Mine mine = MineFactory.getMine("\\R");
		assertEquals("R ", mine.makeMove(Move.L).toString());
		assertEquals(" R", mine.makeMove(Move.L).makeMove(Move.R).toString());
	}
	
	@Test
	public void testMoveToLambdaRobotScore() {
		Mine mine = MineFactory.getMine("\\R");
		assertEquals(1, mine.getLambdaCells().size());
		assertEquals(49, mine.makeMove(Move.L).getScore());
		assertEquals(0, mine.makeMove(Move.L).getLambdaCells().size());
	}
	
	@Test
	public void testPushRockLeft() {
		Mine mine = MineFactory.getMine(" *R");
		assertEquals("*R ", mine.makeMove(Move.L).toString());
	}
	
	@Test
	public void testPushRockRight() {
		Mine mine = MineFactory.getMine("R* ");
		assertEquals(" R*", mine.makeMove(Move.R).toString());
	}
	
	@Test
	public void testInitialState() {
		Mine mine = MineFactory.getMine("R");
		assertEquals(RobotState.NORMAL, mine.getState());
	}
	
	@Test
	public void testAbort() {
		Mine mine = MineFactory.getMine("R");
		Mine newMine = mine.makeMove(Move.A);
		assertEquals(RobotState.ABORTED, newMine.getState());
		assertEquals(0, newMine.getScore());
	}
	
	@Test
	public void testMoveEarthAndDie() {
		Mine mine = new Mine(1, Cell.ROBOT, Cell.EARTH, Cell.EMPTY, Cell.ROCK);
		Mine newMine = mine.makeMove(Move.U);
		assertEquals(RobotState.CRUSHED, newMine.getState());
	}
	
	@Test
	public void testDeathByRockFall() {
		Mine mine = new Mine(2, Cell.ROCK, Cell.EMPTY, Cell.LAMBDA, Cell.ROBOT, Cell.ROCK, Cell.EMPTY);
		Mine end = mine.makeMove(Move.D);
		assertEquals(RobotState.CRUSHED, end.getState());
	}
	
}