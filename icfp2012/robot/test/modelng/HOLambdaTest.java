package modelng;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;
import model.RobotState;

import org.junit.Test;

public class HOLambdaTest {

	@Test
	public void testParsingMap1() {
		Mine mine = MineFactory.getMineFromResource("/horock1.map.txt");
		assertEquals(Cell.HO_LAMBDA, mine.get(4, 4));
		assertEquals(4, mine.getCellCount(Cell.HO_LAMBDA));
		assertEquals(4, mine.getHigherOrderLambaCount());
		assertEquals(11, mine.getInitialAllLambdaCount());
	}

	@Test
	public void testFalling1() {
		Mine mine = MineFactory.getMine("@\n ");
		assertEquals(" \n\\", mine.makeNullMove().toString());
	}

	@Test
	public void testFalling2() {
		Mine mine = MineFactory.getMine("@ \n* ");
		assertEquals(1, mine.getHigherOrderLambaCount());
		assertEquals("  \n*\\", mine.makeNullMove().toString());
		assertEquals(0, mine.makeNullMove().getHigherOrderLambaCount());
		assertEquals(1, mine.makeNullMove().getInitialAllLambdaCount());
		assertArrayEquals(new Integer[] { 1 }, mine.makeNullMove()
				.getLambdaCells().toArray(new Integer[0]));
	}

	@Test
	public void testLiftNotOpenAfterCrash() {
		Mine mine = MineFactory.getMine(
				"#####\n" + 
				"#R\\ L\n" + 
				"#@ *#\n" + 
				"#* *#\n" + 
				"#####");
		Mine end = mine.makeMoves(Path.fromString("RR"));
		assertEquals(
				"#####\n" + 
				"#  RL\n" + 
				"#   #\n" + 
				"#***#\n" + 
				"#####",
				end.toString());
		assertEquals(0, end.getLambdaCells().size());
		assertEquals(2, end.getInitialAllLambdaCount());
		assertEquals(1, end.getRobot().getLambdaCollected());
	}
	
	@Test
    public void testLiftOpenAfterCrash() {
		Mine mine = MineFactory.getMine("#####\n" +
                "#R\\ L\n" +
                "#* @#\n" +
                "#* *#\n" +
                "#####");
        Mine end = mine.makeMoves(Path.fromString("RDD"));
        assertEquals("#####\n" +
                "#   O\n" +
                "#   #\n" +
                "#*R*#\n" +
                "#####",end.toString());
        assertEquals(0, end.getLambdaCells().size());
        assertEquals(2, end.getInitialAllLambdaCount());
        assertEquals(2, end.getRobot().getLambdaCollected());
    }

	
	@Test
    public void testLambdaCrashOnEarth() {
		Mine mine = MineFactory.getMine("#####\n" +
                "#R  L\n" +
                "#@  #\n" +
                "#*  #\n" +
                "#...#\n" +
                "#####");
		Mine end = mine.makeMoves(Path.fromString("WWW"));
        assertEquals("#####\n" +
                "#R  L\n" +
                "#   #\n" +
                "#*\\ #\n" +
                "#...#\n" +
                "#####",end.toString());
    }
	
	@Test
	public void testRobotDiesWhenHOFalls() {
		Mine mine = MineFactory.getMineFromResource("/horock1.map.txt").makeMoves(Path.fromString("LDDDDLDULDLUUUURRRRRRDUDDDDDL"));
		Mine end = mine.makeMove(Move.D);
		assertEquals(RobotState.CRUSHED, end.getState());
	}
}