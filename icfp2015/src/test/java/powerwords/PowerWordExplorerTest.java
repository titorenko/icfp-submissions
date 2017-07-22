package powerwords;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Geometry;

public class PowerWordExplorerTest {
	@Test
	public void testAngleDistance() {
		assertEquals(1, Geometry.angleDistance(1, 2));
		assertEquals(2, Geometry.angleDistance(5, 1));
		assertEquals(0, Geometry.angleDistance(7, 1));
		assertEquals(0, Geometry.angleDistance(1, 7));
	}
}
