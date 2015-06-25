package algo.simple.heuristics;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeltaTest {
	
	@Test
	public void testLessThan() {
		assertTrue(new Delta(-1).isLessThan(new Delta(0)));
		assertFalse(new Delta(0).isLessThan(new Delta(0)));
		assertTrue(new Delta(10, true).isLessThan(new Delta(0)));
		assertFalse(new Delta(-1).isLessThan(new Delta(0, true)));
		assertTrue(new Delta(-1, true).isLessThan(new Delta(0, true)));
	}
}
