package modelng;

import static org.junit.Assert.assertEquals;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Test;

public class ScoreTest {
	
	@Test
	public void testScoreMap5() {
		Mine mine = MineFactory.getMineFromResource("/contest5.map.txt");
        Mine end = mine.makeMoves(Path.fromString("LLUUURUUUURDDRRRRRUULLLLDDDRDRRRRDDDLL"));
        assertEquals(812, end.getScore());
	}
}
