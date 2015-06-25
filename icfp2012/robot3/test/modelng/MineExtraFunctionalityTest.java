package modelng;

import static org.junit.Assert.assertEquals;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Test;

public class MineExtraFunctionalityTest {
	@Test
	public void testFastForward() {
		Mine mine = new Mine(1, Cell.EMPTY, Cell.EMPTY, Cell.ROCK);
		assertEquals(" \n*\n ", mine.makeNullMove().toString());
		assertEquals(" \n \n*", mine.makeNullMove().makeNullMove().toString());
	}
	
	
	@Test
	public void testMakeMovesNormal() {
		Mine mine = MineFactory.getMine("   R");
		assertEquals(" R  ", mine.makeMoves(Path.fromString("LUL")).toString());
	}
	
	@Test
	public void testMakeMovesWithDeath() {
		Mine mine = MineFactory.getMine("* \n  \n R");
		assertEquals("  \n* \nR ", mine.makeMoves(Path.fromString("LRU")).toString());
	}
}
