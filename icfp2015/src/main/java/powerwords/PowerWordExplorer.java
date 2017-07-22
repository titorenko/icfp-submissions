package powerwords;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algo.MinPQ;
import algo.SearchNode;
import model.Geometry;
import powerwords.Powerword.PrefixInfo;

//TODO: add tests for it
//we try to maximize cost function
public class PowerWordExplorer implements Comparator<SearchNode>{
    
    
    private final MinPQ<SearchNode> queue;
    private final SearchNode from;
    private final SearchNode to;
    private final Map<SearchNode, Integer> visited = new HashMap<>();
    private PowerMove bestTarget = null;
    
    public PowerWordExplorer(SearchNode from, SearchNode to) {
        this.from = from;
        this.to = to;
        this.queue = new MinPQ<>(from.getBoard().getWidth()*from.getBoard().getHeight(), this);
        this.queue.insert(from);
    }

    public PowerMove findPowerWordPathFromTo() {
        do {
            runSearchIteration();
        } while (!stopCondition());
        assert bestTarget == null || bestTarget.isEquivalentTo(to);        
        return bestTarget;
    }

    private void runSearchIteration() {
        SearchNode v1 = queue.delMin();

        if (heuristic(v1) < Integer.MIN_VALUE/10) return;
        Integer prevScore = visited.get(v1);
        if (prevScore != null && prevScore >= v1.getPowerScore()) {
        	return;
        }
        if (hasCycle(v1, 1)) return;
        
        boolean isOnTarget = checkIfOnTargetAndRecord(v1);
        if (isOnTarget) return;
        
        visited.put(v1, v1.getPowerScore());
        
        getNext(v1).forEach(v2 -> queue.insert(v2));
    }
    
    private boolean hasCycle(SearchNode v2, int depth) {//TODO improve me
        Set<SearchNode> visited = new HashSet<>();
        int targetIndex = v2.getBoard().getSourceIndex() - depth;
        SearchNode cur = v2;
        while (cur.prev !=null && cur.prev.getBoard().getSourceIndex() > targetIndex) {
            boolean didNotContain = visited.add(cur);
            if (!didNotContain) return true;
            cur = cur.prev;
        }
        return visited.contains(from);
    }

    private List<SearchNode> getNext(SearchNode v1) {
        List<SearchNode> result = v1.neighboursNoPlacement();
        for (Powerword pw : Powerwords.INSTANCE) {
            Object afterPw = pw.applyTo(v1);
            if (afterPw == null) continue;
            if (afterPw instanceof SearchNode) {
            	result.add((SearchNode) afterPw);
            } else if (from.config.doCrossLevelPowerwordSearch){
            	PrefixInfo pi = (PrefixInfo) afterPw;
            	if (!hasCycle(pi.finalPosition, 2) && Geometry.dist(to, pi.placedPosition.prev) == 0) {
            		MultisourcePowerMove msm = new MultisourcePowerMove(pi.placedPosition, pi.finalPosition);
            		if (bestTarget == null || msm.getPowerScore() > bestTarget.getPowerScore()) {
                        bestTarget = msm;
                    }
            	}
            }
        }
        return result;
    }

    private boolean checkIfOnTargetAndRecord(SearchNode sn) {
        if (Geometry.dist(to, sn) == 0) {
        	sn.neighboursNoPlacement();// init next when locked
            if (sn.canBeLocked() && (bestTarget == null || sn.getPowerScore() > bestTarget.getPowerScore())) {
                bestTarget = new SimplePowerMove(sn.nextWhenLocked);
            }
            return true;
        }
        return false;
    }

    private boolean stopCondition() {
        return queue.isEmpty();
    }

    @Override
    public int compare(SearchNode o1, SearchNode o2) {
        return heuristic(o2) - heuristic(o1);
    }

    private int heuristic(SearchNode sn) {
        return sn.getPowerScore() - Geometry.dist(to, sn);
    }
}