package algo.simple.heuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Mine;
import model.MineFactory;
import model.Move;

import org.junit.Test;


public class ClosedLambdaPenaltyTest {
	/*String spec=
	"###############\n"+
	"#             #\n"+
	"#  # #####  *##\n"+
	"#  #    .*##* #\n"+
	"# #####R...## #\n"+
	"# ......####  #\n"+
	"# .######* #  #\n"+
	"# .#. *...## ##\n"+
	"# ##. ..  * *.#\n"+
	"#  ..... L# #.#\n"+
	"########### #.#\n"+
	"#           #.#\n"+
	"## ##########.#\n"+
	"#  #\\.........#\n"+
	"###############";
	
	@Test
	public void testLambdaPenalized() {
		Mine mine = MineFactory.getMine(spec);
		BatchedClosedCellEvaluator e = new BatchedClosedCellEvaluator(mine);
		int lambda = mine.getLambdaCells().get(0);
		e.reset(mine);
		assertTrue(e.isClosed(lambda));
	}
	
	@Test 
	public void testRestrictedLiftPenalized() {
		Mine mine = MineFactory.getMineFromResource("/contest8.map.txt", "UULLDDRLULLLL");
		ClosedLambdaLiftPenalty p = new ClosedLambdaLiftPenalty(mine);
		Delta delta = p.getDelta(mine, new HeuristicEngineContext());
		assertFalse(delta.isDeadEnd());
		assertTrue("Delta: "+delta, delta.getValue() < -50);
		
	}*/
	
	@Test
	public void testRobotInside() {
		Mine mine = MineFactory.getMineFromResource("/contest3.map.txt", "LDDDRRLRRRDDLLL");
		ClosedLambdaLiftPenalty p = new ClosedLambdaLiftPenalty();
		Delta delta = p.getDelta(mine, new HeuristicEngineContext());
		assertEquals(0, delta.getValue());
		assertFalse(delta.isDeadEnd());
	}
	
	/*@Test
	public void testClosedLift() {
		Mine mine = MineFactory.getMineFromResource("/flood4.map.txt", "DDDDLDDRRRRUUUUUUUUULLLLLLULULLLLLLUURRDDDDDDLLLLLLLRRRRRRRRRRDDDDDRRRRRRRRRRRRRRRRURRRRDRRUDRRULUUR");
		ClosedLambdaLiftPenalty p = new ClosedLambdaLiftPenalty();
		Delta delta = p.getDelta(mine, new HeuristicEngineContext());
		assertTrue(delta.isDeadEnd());
	}*/
	
	@Test
	public void testLiftOnMap2() {
			Mine mine = MineFactory.getMineFromResource("/contest2.map.txt", "RR");
			ClosedLambdaLiftPenalty p = new ClosedLambdaLiftPenalty();
			Delta deltaUp = p.getDelta(mine.makeMove(Move.U), new HeuristicEngineContext());
			Delta deltaLeft = p.getDelta(mine.makeMove(Move.L), new HeuristicEngineContext());
			assertTrue(deltaLeft.getValue() < -100);
			assertTrue(deltaLeft.isDeadEnd());
			assertEquals(Delta.NULL, deltaUp);
	}
}
