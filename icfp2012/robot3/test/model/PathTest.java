package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathTest {
	@Test
	public void subpathTest() {
		Path path = Path.fromString("LRW");
		assertEquals("RW", path.subpath(1).toString());
		assertEquals("", path.subpath(3).toString());
	}
	
	@Test
	public void prefixTest() {
		Path path1 = Path.fromString("LRW");
		Path path2 = Path.fromString("LR");
		assertTrue(path2.isPrefixOf(path1));
		assertFalse(path1.isPrefixOf(path2));
	}
}
