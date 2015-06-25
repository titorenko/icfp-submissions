package modelng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;

import org.junit.Test;

public class TrampolineTest {
	@Test
	public void testMap1() {
		Mine mine = MineFactory.getMineFromResource("/trampoline1.map.txt");
		String expected = 
		"############     \n"+
		"#..*.R..*..#     \n"+
		"#..A....B..######\n"+
		"#....2.. ..#\\\\\\C#\n"+
		"#......* *.#\\\\\\1#\n"+
		"########L########";
		assertEquals(expected, mine.toString());
		
		Cell cell = mine.get(3, 3);
		assertEquals(Cell.TRAMPOLINE_A, cell);
		assertEquals(mine.index(15, 1), mine.getTarget(mine.index(3, 3)));
		assertEquals(2, mine.getSources(mine.index(15, 1)).size());
	}
	
	@Test
	public void testCanMoveIntoAndMove() {
		Mine mine = MineFactory.getMineFromResource("/trampoline1.map.txt");
		mine = mine.makeMoves(Path.fromString("DL"));
		assertTrue(mine.getPossibleRobotMoves().contains(Move.L));
		mine = mine.makeMove(Move.L);
		assertEquals(mine.index(15, 1), mine.getRobotCell());
		assertEquals(-1, mine.findCell(Cell.TRAMPOLINE_A));
		assertEquals(-1, mine.findCell(Cell.TARGET_1));
		assertEquals(-1, mine.findCell(Cell.TRAMPOLINE_B));
	}
}
