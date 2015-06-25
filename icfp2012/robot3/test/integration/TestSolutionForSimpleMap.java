package integration;


import static model.Move.A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Before;
import org.junit.Test;

public class TestSolutionForSimpleMap {
	
	
	private Mine mine;

	@Before
	public void init() {
		String mapEncoded= 
			"######"+"\n"+
			"#. *R#"+"\n"+
			"#  \\.#"+"\n"+
			"#\\ * #"+"\n"+
			"L  .\\#"+"\n"+
			
			"######";
		this.mine = MineFactory.getMine(mapEncoded);
	}
	
	@Test
	public void testSolution() {
		Mine end = mine.makeMoves(Path.fromString("DLRDDUULLLDDL"));
		assertTrue(end.getState().isFinished());
		assertEquals(212, end.getScore());
	}

	@Test
	public void testSolutionAbort() {
		Mine end = mine.makeMove(A);
		assertTrue(end.getState().isFinished());
		assertEquals(0, mine.getScore());
	}

	@Test
	public void testSolutionWalkInWall() {
		Mine end = mine.makeMoves(Path.fromString("RRA"));
		assertTrue(end.getState().isFinished());
		assertEquals(-2, end.getScore());
	}

    @Test
    public void testSolutionIdle()  {
        Mine end = mine.makeMoves(Path.fromString("WWA"));
        assertTrue(end.getState().isFinished());
		assertEquals(-2, end.getScore());
    }

    @Test
    public void testSolutionX()  {
      	Mine mine = MineFactory.getMineFromResource("/contest2.map.txt");
        Mine end = mine.makeMoves(Path.fromString("UUURRRRA"));
        assertTrue(end.getState().isFinished());
		assertEquals(143, end.getScore());
    }

    @Test
	public void testSolution1() {
		Mine mine = MineFactory.getMineFromResource("/contest1.map.txt");
		Mine end = mine.makeMoves(Path.fromString("LDLDDA"));
		assertTrue(end.getState().isFinished());
		assertEquals(45, end.getScore());
	}

    @Test
    public void testSolution2()  {
        Mine mine = MineFactory.getMineFromResource("/contest9.map.txt");
        Mine end = mine.makeMoves(Path.fromString("DLUURRUULWWLLWLLUDURDWUDLDWDULRWLUUDLRWRLWDDURLDRWDLRLRDUUURWWLDRDDUUDRDUURWRWRWRUULULULDDWWWDDUDUDWLLWWUDLRWLRDDRRLRLRRRWDURDURLLWWRWRRURDLWDDRRDWUWDUUDRWRWLUWLULRUUUUWUUWWRUDLUUDLLWURRRUWRRLLDULWLUUDDRLRRULRRRLWUDRRURUWRDRUDWURLLLURUWULWLLUDRUUUDWUWDURLUWWDLWUWUURWLDRULDRRRDDLWDUWRDWLRURWUUURWRDDLURDRDRRRUWLRRLURULULDUDRRLWUWLWLLDWDWLWDWDWRDDA"));
        assertTrue(end.getState().isFinished());
        assertEquals(454, end.getScore());
    }

    @Test
    public void testRockFall() {
        Mine end = mine.makeMoves(Path.fromString("LA"));
        assertTrue(end.getState().isFinished());
        assertEquals(-1, end.getScore());
    }

  /*  @Test
    public void testWaterRises() {
        String mapEncoded =
            "######"+"\n"+
            "#. * #"+"\n"+
            "#  \\.#"+"\n"+
            "#\\ * #"+"\n"+
            "LR .\\#"+"\n"+
            "######";
        Game game = MineFactory.getGame(mapEncoded, new ElementsConfig(-1,1,1));
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("WWWW"));
        assertTrue(end.getState().isFinished());
        assertEquals(-3, robot.getFinalScore());
    }

    @Test
    public void testWaterResistance() {
        String mapEncoded =
            "######"+"\n"+
            "#. * #"+"\n"+
            "#  \\.#"+"\n"+
            "#\\ * #"+"\n"+
            "LR .\\#"+"\n"+
            "######";
        Game game = MineFactory.getGame(mapEncoded, new ElementsConfig(1,1,3));
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("WWWWW"));
        assertTrue(end.getState().isFinished());
        assertEquals(-4, robot.getFinalScore());
    
    }

    @Test
    public void testWaterResistanceReset() {
        String mapEncoded =
            "######"+"\n"+
            "#. * #"+"\n"+
            "#  \\.#"+"\n"+
            "#  * #"+"\n"+
            "LR .\\#"+"\n"+
            "######";
        Game game = MineFactory.getGame(mapEncoded, new ElementsConfig(1,Integer.MAX_VALUE,3));
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("WWWUDWWWW"));
        assertTrue(end.getState().isFinished());
        assertEquals(-8, robot.getFinalScore());
    }

    @Test
    public void testBeardGrowth1() {
        Game game = MineFactory.getGameFromResource("/beard1.map.txt");
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("RRDDRDRDDUUUUWWWWWWWWWWWWUWWWW"));
        assertEquals("##########\n" +
                "#*   \\\\\\\\#\n" +
                "#.    R  #\n" +
                "# \\  .  \\#\n" +
                "#! *   *!#\n" +
                "####   # #\n" +
                "#\\\\... # L\n" +
                "#\\\\.W. . #\n" +
                "#\\\\.WW*  #\n" +
                "##########", end.toString());
    }

    @Test
    public void testBeardGrowth1A() {
        Game game = MineFactory.getGameFromResource("/beard1.map.txt");
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("RRDDRDRDDUUUUWWWWWWWWWWWWUWWWWWWWWWWWWWWWWWWW"));
        assertEquals("##########\n" +
                "#*   \\\\\\\\#\n" +
                "#.    R  #\n" +
                "# \\  .  \\#\n" +
                "#! *   *!#\n" +
                "####   # #\n" +
                "#\\\\... # L\n" +
                "#\\\\.W.W. #\n" +
                "#\\\\.WW*  #\n" +
                "##########", end.toString());
    }

    @Test
    public void testBeardGrowth2() {
        Game game = MineFactory.getGameFromResource("/beard1.map.txt");
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("RRDDRDRDDUUUUWWWWWWWWWWWWWUWWW"));
        assertEquals("##########\n" +
                "#*   \\\\\\\\#\n" +
                "#.    R  #\n" +
                "# \\  .  \\#\n" +
                "#! *   *!#\n" +
                "####   # #\n" +
                "#\\\\... # L\n" +
                "#\\\\.W.*. #\n" +
                "#\\\\.WWW  #\n" +
                "##########",end.toString());
    }

    @Test
    public void testBeardGrowth3() {
        Game game = MineFactory.getGameFromResource("/beard1.map.txt");
        Robot robot = game.getRobot();
        Mine end = game.getMine().makeMoves(robot, MineFactory.getMoves("RRDDRDRDDUUUUWWWWWWWWWWWWWWUWW"));
        assertEquals("##########\n" +
                "#*   \\\\\\\\#\n" +
                "#.    R  #\n" +
                "# \\  .  \\#\n" +
                "#! *   *!#\n" +
                "####   # #\n" +
                "#\\\\...*# L\n" +
                "#\\\\.W.W. #\n" +
                "#\\\\.WWW  #\n" +
                "##########",end.toString());
    }*/
}
