package algo.mst;

import static org.junit.Assert.*;

import org.junit.Test;

public class CandidateFinderTest {
	
	@Test
	public void testOctants() {
		CandidateFinder finder = new CandidateFinder();
		assertEquals(0, finder.getOctant(3, 1, 0, 0));
		assertEquals(1, finder.getOctant(1, 3, 0, 0));
		assertEquals(3, finder.getOctant(-3, 1, 0, 0));
		assertEquals(7, finder.getOctant(3, -1, 0, 0));
		assertEquals(6, finder.getOctant(1, -3, 0, 0));
		assertEquals(4, finder.getOctant(-3, -1, 0, 0));
		assertEquals(5, finder.getOctant(-1, -3, 0, 0));
	}
}
