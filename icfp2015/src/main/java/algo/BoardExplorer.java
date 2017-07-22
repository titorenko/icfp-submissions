package algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import model.Board;

public class BoardExplorer {
    
    private final Queue<SearchNode> q = new LinkedList<>();
    private final Set<SearchNode> visited = new HashSet<>();
    
    private final SearchNode initial;

    public BoardExplorer(SearchNode initial) {
        this.initial = initial;
        
        int idx = initial.getBoard().getSourceIndex();
        SearchNode cur = initial;
        while (cur.prev !=null && cur.prev.getBoard().getSourceIndex() == idx) {
        	visited.add(cur.prev);
        	cur = cur.prev;
        }
    }
    
    
	public Map<Integer, SearchNode> computeReachableFinalNodesWithScores() {
        visit(initial);
        
        while(!q.isEmpty()) {
            SearchNode v1= q.poll();
            v1.neighboursNoPlacement().forEach(v2 -> {
                if (!visited.contains(v2)) {
                    visit(v2);
                }
            });
        }
        return visitedToBoardIndexedList();
    }

	private void visit(SearchNode sn) {
		q.add(sn);
		visited.add(sn);
	}

    private Map<Integer, SearchNode> visitedToBoardIndexedList() {
        Map<Integer, SearchNode> result = new HashMap<>();
        for (SearchNode sn : visited) {
            if (!sn.canBeLocked()) continue;
            Board board = sn.getBoard();
            int idx = board.toIndex(board.getTrueSourceX(), board.getTrueSourceY());
            SearchNode bestSoFarForIdx = result.get(idx);
            if (bestSoFarForIdx == null) {
            	result.put(idx, sn);
            } else {
                int maxScore = bestSoFarForIdx.heuristicScore();
                if (sn.heuristicScore() > maxScore) {
                    result.put(idx, sn);
                }
            }
        }
        return result;
    }
}