package algo;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import icfp.io.Parser;
import icfp.io.model.Problem;
import model.Board;
import powerwords.PowerWordExplorer;
import runner.SolverConfig;

public class BellmanFordBoardExplorerTest {
    @Test
    public void testBoard21() { //TODO: extend me
        String name = "/problems/problem_21.json";
        Parser parser = new Parser();
        Problem p = parser.parse(name);
        SearchNode initial = SearchNode.initial(p.createBoards().get(0), SolverConfig.QUICK);
        BellmanFordBoardExplorer e = new BellmanFordBoardExplorer(initial);
        List<SearchNode> bfNodes = e.computeReachableFinalNodesWithScores();
        Map<Integer, SearchNode> reachable = new BoardExplorer(initial).computeReachableFinalNodesWithScores();
        for (SearchNode bfNode : bfNodes) {
            Board board = bfNode.prev.getBoard();
            int idx = board.toIndex(board.getTrueSourceX(), board.getTrueSourceY());
            SearchNode breadthFirstNode = reachable.get(idx);
            assertNotNull("No equivalent for bellman node with idx "+idx+": "+bfNode, breadthFirstNode);
            assertEquivalent(initial, bfNode, breadthFirstNode);
            reachable.remove(idx);
        }
        assertTrue("Expected empty, but was: "+reachable.entrySet(), reachable.isEmpty());
    }

    private void assertEquivalent(SearchNode initial, SearchNode bfNode, SearchNode breadthFirstNode) {
        PowerWordExplorer e = new PowerWordExplorer(initial, breadthFirstNode);
        int pwScore = e.findPowerWordPathFromTo().getPowerScore();
        assertEquals(pwScore, bfNode.getPowerScore());
    }
}
