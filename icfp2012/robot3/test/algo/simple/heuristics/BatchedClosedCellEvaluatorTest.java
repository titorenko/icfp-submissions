/*package algo.simple.heuristics;

import static org.junit.Assert.*;
import model.Mine;
import model.MineFactory;
import model.Move;

import org.junit.Test;

public class BatchedClosedCellEvaluatorTest {
	
	private Mine mine;
	private BatchedClosedCellEvaluator ce;

	@Test
	public void testSimpleUpNegativeCase() {
		init( 
		"#*#\n"+
		"#.#\n"+
		"#\\#");
		assertFalse(ce.isPossible(1, 2, Move.U, true));
	}
	
	@Test
	public void testSimpleUpPositiveCase() {
		init( 
		" * \n"+
		"#.#\n"+
		"#\\#");
		assertTrue(ce.isPossible(1, 2, Move.U, true));
	}
	
	@Test
	public void testSimpleLeftNegativeCase() {
		init( 
		".*.#\n"+
		".#\\#");
		assertFalse(ce.isPossible(1, 1, Move.L, true));
	}
	
	@Test
	public void testSimpleLeftPositiveCase() {
		init( 
		".*.#\n"+
		"..\\#");
		assertTrue(ce.isPossible(1, 1, Move.L, true));
	}
	
	@Test
	public void testSimpleRightNegativeCase() {
		init( 
		"####\n"+
		"#.*.\n"+
		"#\\#.");
		assertFalse(ce.isPossible(2, 1, Move.R, true));
	}
	
	@Test
	public void testSimpleRightPositiveCase() {
		init( 
		"####\n"+
		"#.*.\n"+
		"#\\..");
		assertTrue(ce.isPossible(1, 1, Move.R, true));
	}
	
	@Test
	public void testSimpleDownNegativeCase() {
		init( 
		"#\\#\n"+
		" *#\n" +
		" # ");
		assertFalse(ce.isPossible(1, 1, Move.D, true));
	}
	
	@Test
	public void testSimpleDownPositiveFallingCase() {
		init( 
		"#\\#\n"+
		" *#\n" +
		"   ");
		assertTrue(ce.isPossible(1, 1, Move.D, true));
	}
	
	@Test
	public void testSimpleDownPositiveShiftCase() {
		init( 
		"#\\#\n"+
		" *.\n" +
		" # ");
		assertTrue(ce.isPossible(1, 1, Move.D, true));
	}
	
	@Test
	public void testDoubleRockLeftPositive() {
		init( 
		    "####\n"+
		    ".*.#\n"+
			".*.#\n"+
			"..\\#");
		assertTrue(ce.isPossible(1, 2, Move.L, true));
	}

	//TODO: add tests  + consider map 4
	private void init(String spec) {
		mine = MineFactory.getMine(spec);
		ce = new BatchedClosedCellEvaluator(mine);
		ce.reset(mine);
	}
}
*/