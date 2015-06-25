package algo.mst;

import java.util.List;

import model.Mine;
import model.MineFactory;

import org.junit.Test;

public class AStarRealMapTests {
	@Test
	public void testHO3() {
		Mine ho3 = MineFactory.getMineFromResource("/horock3.map.txt");
		
		
		AStarConnectionFinder aStar = new AStarConnectionFinder();
		List<Edge> connections = aStar.findConnections(ho3, ho3.getLambdaCells());
		for (Edge edge : connections) {
			
			System.out.println(edge+" "+edge.getEndPath());
		}
	}
	
	@Test
	public void testMap9() {
		Mine m9 = MineFactory.getMineFromResource("/contest9.map.txt", "LURURRRDULLLDDRRRRRRRUURURRRRLDDDRRRRRRRRRRRUULLLDLULL");
		AStarConnectionFinder aStar = new AStarConnectionFinder();
		Edge connections = aStar.findConnection(m9, 257);
		System.out.println(connections);
		
	}
}
