package algo.astar;

import model.Cell;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Test;

public class AStarRealMapTests {
	@Test
	public void testHO3() {
		Mine ho3 = MineFactory.getMineFromResource("/horock3.map.txt");
		
		int beard = ho3.findCell(Cell.BEARD);
		ho3.set(beard % ho3.getWidth(), beard / ho3.getWidth(), Cell.EMPTY);
		
		int lift = ho3.findCell(Cell.CLOSED_LIFT);
		ho3.set(lift % ho3.getWidth(), lift / ho3.getWidth(), Cell.OPEN_LIFT);
		
		
		AStar aStar = new AStar(ho3, lift);
		Path path = aStar.findPathToSquare();
		System.out.println(path);
	}
}
