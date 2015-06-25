package algo.simple.heuristics;

import static org.junit.Assert.*;
import model.Mine;
import model.MineFactory;

import org.junit.Test;

import algo.mst.HeuristicalEngineFactory;

public class DeadEndTests {
	
	@Test
	public void testRegression1() {
		Mine mine = MineFactory.getMineFromResource("/contest3.map.txt", "LDDDRRRLLLUUULRDDDRRRRDDLLLLL");
		HeuristicalEngine engine = HeuristicalEngineFactory.getEngine(mine);
		Delta delta = engine.applyHeuristics(mine);
		assertFalse(delta.isDeadEnd());
	}
	
	@Test
	public void testRegression2() {
		Mine mine = MineFactory.getMineFromResource("/beard4.map.txt", "LL");
		HeuristicalEngine engine = HeuristicalEngineFactory.getEngine(mine);
		Delta delta = engine.applyHeuristics(mine);
		System.out.println(delta);
	}
}
