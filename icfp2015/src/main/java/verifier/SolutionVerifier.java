package verifier;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import algo.SearchNode;
import model.Board;
import model.MoveEncoding;
import runner.SolverConfig;

public class SolutionVerifier {
    public boolean verifyNoRepetitions(String moveEncoding, final Board initial) {
        SearchNode cur = SearchNode.initial(initial, SolverConfig.QUICK);
        Set<SearchNode> visited = new HashSet<>();
        visited.add(cur);
        
        for (char me : moveEncoding.toCharArray()) {
            if (cur.getBoard().isFinished()) {
                throw new IllegalStateException("Board is finished but more moves remain:\n"+cur);
            }
            MoveEncoding move = MoveEncoding.encodingOf(me);
            cur = cur.next(Pair.of(cur.getBoard().makeMove(move), move));
            if (visited.contains(cur)) {
            	throw new IllegalStateException("Board "+cur+" was repeated");
            }
            visited.add(cur);
        }
        return true;
    }
}