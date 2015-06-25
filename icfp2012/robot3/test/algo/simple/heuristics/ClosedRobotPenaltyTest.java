package algo.simple.heuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Mine;
import model.MineFactory;
import model.Path;

import org.junit.Test;

import algo.simple.EvaluationContext;
import algo.simple.EvaluatorFactory;

public class ClosedRobotPenaltyTest {
	@Test
	public void testSimpleRobotStucknessIsPenalized() {
		Mine mine2 = MineFactory.getMineFromResource("/contest2.map.txt");
		Mine mine = mine2.makeMoves(Path.fromString("UUUUDDRURRR"));
		EvaluationContext ctx = EvaluatorFactory.getContext(mine);
		Delta delta = ctx.tesHeuristics(mine);
		assertTrue("Expected penalty for stuck robot, but got: " + delta, delta.getValue() < 50);
	}
	
	@Test
	public void testWhenRobotIsLimitedButNotStucknNoPenalty() {
		Mine mine = MineFactory.getMineFromResource("/contest9.map.txt", "LURURRRDULLLDDRRR");
		ClosedRobotPenalty closedRobotPenalty = new ClosedRobotPenalty(mine);
		Delta delta = closedRobotPenalty.getDelta(mine, new HeuristicEngineContext());
		assertEquals(Delta.NULL, delta);
	}
	
	@Test
	public void testRobotStuckInRelativelyLargeAreaPenalty() {
		Mine mine = MineFactory.getMineFromResource("/contest9.map.txt");
		mine = mine.makeMoves(Path.fromString("LURURRRDULLLDDRL"));
		//long start = System.currentTimeMillis();
		//for (int i=0; i<1000000;i++) {
		ClosedRobotPenalty closedRobotPenalty = new ClosedRobotPenalty(mine);
		Delta delta = closedRobotPenalty.getDelta(mine, new HeuristicEngineContext());
		assertTrue("Delta was: "+delta, delta.getValue() < -100);
		assertTrue(delta.isDeadEnd());
		//}System.out.println(System.currentTimeMillis()- start);
	}
	
	@Test
	public void testRegressionOnMap8() {
		Mine mine = MineFactory.getMineFromResource("/contest8.map.txt", 
				"UULLDDRLULLLUDDUULUDDUULDDUULDULDURRDDDUUURRDDDLUUURRDURDURUUUUURUUUUUUULLLLLLLLRRDDDDDDDDDDLLRRUUULURLLUUDDDDUURRUUUUUURRDDDDDURURUDDUURUDDLDDUURRURUUDDDDDRRR");
		ClosedRobotPenalty closedRobotPenalty = new ClosedRobotPenalty(mine);
		Delta delta = closedRobotPenalty.getDelta(mine, new HeuristicEngineContext());
		assertTrue("Delta was: "+delta, delta.getValue() < -500);
		assertTrue(delta.isDeadEnd());
	}
	
	@Test
	public void testWithTrampolines() {
		Mine mine = MineFactory.getMineFromResource("/trampoline3.map.txt", "RRRRLDDDDDDRDDDLLRRRRUURRRRRUUURRRRUURRRRDDURRDDURLL");
		ClosedRobotPenalty closedRobotPenalty = new ClosedRobotPenalty(mine);
		Delta delta = closedRobotPenalty.getDelta(mine, new HeuristicEngineContext());
		assertTrue("Delta was: "+delta, delta.getValue() < -500);
		assertTrue(delta.isDeadEnd());
	}
	
	@Test
	public void testProblemWithStucknessWhenObstaclesAreMoving() {
		Mine mine = MineFactory.getMineFromResource("/trampoline2.map.txt", "RRRLDDDRRRULLULUUURRULULLLDLDLLLLD");
		ClosedRobotPenalty closedRobotPenalty = new ClosedRobotPenalty(mine);
		Delta delta = closedRobotPenalty.getDelta(mine, new HeuristicEngineContext());
		assertEquals(0, delta.getValue());
		assertFalse(delta.isDeadEnd());
				
	}
}
