package algo.simple.heuristics;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Mine;
import model.MineFactory;

import org.junit.Test;

import algo.simple.lookahead.HeuristicEngineContext;

public class ClosedLambdaPenaltyTest {
	String spec=
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
		
	}
	
	@Test
	public void testRobotInside() {
		Mine mine = MineFactory.getMineFromResource("/contest3.map.txt", "LDDDRRLRRRDDLLL");
		ClosedLambdaLiftPenalty p = new ClosedLambdaLiftPenalty(mine);
		Delta delta = p.getDelta(mine, new HeuristicEngineContext());
		assertEquals(0, delta.getValue());
		assertFalse(delta.isDeadEnd());
	}
}
