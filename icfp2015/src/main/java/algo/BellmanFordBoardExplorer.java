package algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import model.Board;
import powerwords.MultisourcePowerMove;
import powerwords.Powerword;
import powerwords.Powerwords;

public class BellmanFordBoardExplorer {
    
    private final Queue<SearchNode> open = new LinkedList<>();
    private final SearchNode[] bestPaths;
    private final MultisourcePowerMove[] msBestPaths;
    
    private final SearchNode initial;
    

    public BellmanFordBoardExplorer(SearchNode initial) {
        this.initial = initial;
        this.bestPaths = new SearchNode[initial.getBoard().getWidth() * initial.getBoard().getHeight() * 6];
        this.msBestPaths = new MultisourcePowerMove[initial.getBoard().getWidth() * initial.getBoard().getHeight() * 6];
        open.add(initial);
    }

	public List<SearchNode> computeReachableFinalNodesWithScores() {
        while(!open.isEmpty()) {
            SearchNode u= open.poll();
            
            expand(u).stream().filter(v -> !hasCycle(v, 1)).forEach(v -> {
                improve(u, v);
            });
        }
        return toList();
    }

    private List<SearchNode> toList() {
        List<SearchNode> result = new ArrayList<>();
        /*        int maxPowerScore = 
                Arrays.stream(bestPaths).filter(p -> p != null).mapToInt(p -> p.getPowerScore()).max().orElse(0);*/
        for (int i = 0; i < bestPaths.length; i++) {
        	SearchNode sn = bestPaths[i];
        	MultisourcePowerMove mpm = msBestPaths[i];
        	SearchNode toAdd = null;
            if (sn != null && sn.canBeLocked()) {
            	toAdd = sn.nextWhenLocked;
            }
            /*if (mpm != null) {
				if (mpm.getPowerScore() > maxPowerScore) {
            	if (toAdd == null || mpm.getPowerScore() > toAdd.getPowerScore()) {
            		toAdd = mpm.getToNode();
            	}
            }*/
            if (toAdd != null) result.add(toAdd);
        }
        return result;
    }

    private List<SearchNode> expand(SearchNode u) {
        List<SearchNode> result = u.neighboursNoPlacement();
        for (Powerword pw : Powerwords.INSTANCE) {
            Object afterPw = pw.applyTo(u);
            if (afterPw == null) continue;
            if (afterPw instanceof SearchNode) {
                result.add((SearchNode) afterPw);
            } else {
            	/*PrefixInfo pi = (PrefixInfo) afterPw;
                if (!hasCycle(pi.finalPosition, 2)) {
                    MultisourcePowerMove msm = new MultisourcePowerMove(pi.placedPosition, pi.finalPosition);
                    int idx = idxOf(pi.placedPosition.prev);
                    SearchNode bestSoFar = bestPaths[idx];
                    if (bestSoFar == null || msm.getPowerScore() > bestSoFar.getPowerScore()) {
                    	MultisourcePowerMove msBestSoFar = msBestPaths[idx];
                    	if (msBestSoFar == null || msm.getPowerScore() > msBestSoFar.getPowerScore()) {
                    		msBestPaths[idx] = msm;
                    	}
                    }
                }*/
            }
        }
        return result;
    }
    
    private void improve(SearchNode u, SearchNode v) {
        int idx = idxOf(v);
        SearchNode bestSoFar = bestPaths[idx];
        if (bestSoFar == null || bestSoFar.getPowerScore() < v.getPowerScore()) {
            bestPaths[idx] = v;
            open.add(v);
        }
    }
    
    private int idxOf(SearchNode v) {
        Board b = v.getBoard();
        int idx = b.toIndex(b.getTrueSourceX(), b.getTrueSourceY())*6+b.getSourceAngle();
        return idx;
    }

    private boolean hasCycle(SearchNode v, int depth) {//TODO improve me
        Set<SearchNode> visited = new HashSet<>();
        int targetIndex = v.getBoard().getSourceIndex() - depth;
        SearchNode cur = v;
        while (cur.prev !=null && cur.prev.getBoard().getSourceIndex() > targetIndex) {
            boolean didNotContain = visited.add(cur);
            if (!didNotContain) return true;
            cur = cur.prev;
        }
        return visited.contains(initial);
    }

}