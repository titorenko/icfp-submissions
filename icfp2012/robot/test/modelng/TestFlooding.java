package modelng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.ElementsConfig;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;
import model.RobotState;

import org.junit.Test;

public class TestFlooding {

	String mineSpec1 =
			"######"+"\n"+
					"L  .\\#"+"\n"+
					"#\\   #"+"\n"+
					"# *\\ #"+"\n"+
					"#.*  #"+"\n"+
					"####R#";
	
	@Test
    public void testFloodRaises() {
		Mine mine = MineFactory.getMine(mineSpec1, new ElementsConfig(-1, 2, 5));
		assertEquals(-1, mine.getFloodingState().getWaterLevel());
		mine = mine.makeMove(Move.W);
		assertEquals(-1, mine.getFloodingState().getWaterLevel());
		mine = mine.makeMove(Move.W);
		assertEquals(0, mine.getFloodingState().getWaterLevel());
		
	}
	@Test
    public void testRobotUnderWater() {
        Mine mine = MineFactory.getMine(mineSpec1, new ElementsConfig(-1, 2, 5));
        assertFalse(mine.isRobotUnderWater());
        mine = mine.makeMove(Move.W);
        assertFalse(mine.isRobotUnderWater());
        mine = mine.makeMove(Move.W);
        assertTrue(mine.isRobotUnderWater());
        mine = mine.makeMove(Move.U);
        assertFalse(mine.isRobotUnderWater());
        mine = mine.makeMove(Move.D);
        assertTrue(mine.isRobotUnderWater());
    }
    
	@Test
	public void testDrowning() {
		Mine mine = MineFactory.getMine("R", new ElementsConfig(0, 10, 1));
		mine = mine.makeMove(Move.W);
		assertEquals(RobotState.NORMAL, mine.getState());     
		assertEquals(1, mine.getFloodingState().getUnderwaterTime());
		mine = mine.makeMove(Move.W);
		assertEquals(RobotState.DROWNED, mine.getState());
	}
	
	@Test
	public void testUnderwaterTime() {
		Mine mine = MineFactory.getMine("R\n ", new ElementsConfig(0, 10, 1));
		mine = mine.makeMove(Move.W);
		assertEquals(0, mine.getFloodingState().getUnderwaterTime());
		mine = mine.makeMove(Move.D);
		assertEquals(1, mine.getFloodingState().getUnderwaterTime());
	}
	
	@Test
    public void testFloodingStateConstructionFromResource() {
    	Mine game = MineFactory.getMineFromResource("/flood1.map.txt");
    	assertEquals(0, game.getFloodingState().getWaterLevel());
    	assertEquals(8, game.getFloodingState().getTimeToNextBump());
    	assertEquals(5, game.getCfg().getWaterproof());
	}
	
    @Test
    public void testDrowningOnMap1() {
    	Mine mine = MineFactory.getMineFromResource("/flood1.map.txt");
    	mine = mine.makeMoves(Path.fromString("LLLLDDRRRLDRDRRDUUDLDLR"));
    	assertEquals(RobotState.NORMAL, mine.getState());
    	assertEquals(RobotState.DROWNED, mine.makeMove(Move.U).getState());
    	assertEquals(RobotState.DROWNED, mine.makeMove(Move.U).makeMove(Move.U).getState());
    }
}
