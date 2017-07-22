package algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import model.Board;
import runner.SolverConfig;

public class GreedySolver {
    private static final int LOG_LEVEL = 0;

    private int bestScore = Integer.MIN_VALUE;
    private SearchNode bestSolution = null;
    private final SolverConfig cfg;
    
    private Queue<SearchNode> queue;
    
    int nIterations;

    private long startTime;

    public GreedySolver(Board initial, SolverConfig cfg) {
        this(initial, cfg, LOG_LEVEL);
    }

    public GreedySolver(Board initial, SolverConfig cfg, int logLevel) {
        ExecutionLog.INSTANCE.setInitial(initial);
        this.cfg = cfg;
        this.queue = new LinkedList<>();
        this.queue.add(SearchNode.initial(initial, cfg));
    }
    
    public SearchNode solve() {
        this.startTime = System.currentTimeMillis();
        nIterations = 0;
        do {
            runSearchIteration();
            nIterations++;
        } while (!stopCondition());
        return bestSolution;
    }

    private void runSearchIteration() {
        SearchNode node = queue.poll();
        if (node.getScore() > bestScore) {
            bestScore = node.getScore();
            bestSolution = node;
            ExecutionLog.INSTANCE.onBestNode(bestScore, bestSolution, 0);
        }
        ExecutionLog.INSTANCE.onNextNode(node);
        List<SearchNode> nextMoves = nextMoves(node);
        if (nextMoves.size() > 0) queue.add(nextMoves.get(0));
    }

    private List<SearchNode> nextMoves(SearchNode node) {
        List<SearchNode> reachable = new BellmanFordBoardExplorer(node).computeReachableFinalNodesWithScores();
        List<Integer> branchingFactors = cfg.schedule;
        List<SearchNode> nodes = selectNodes(reachable, branchingFactors.get(0));
        List<Pair<SearchNode, SearchNode>> leve1WithBestLookahead = new ArrayList<>();
        for (SearchNode level1Node : nodes) {
            SearchNode levelBest = lookAheadSelection(Collections.singletonList(level1Node), 
                    branchingFactors.subList(1, branchingFactors.size()));
            if (levelBest == null) continue;
            leve1WithBestLookahead.add(Pair.of(level1Node, levelBest));
        }
        if (leve1WithBestLookahead.size() == 0) return Collections.emptyList();
        Collections.sort(leve1WithBestLookahead, new Comparator<Pair<SearchNode, SearchNode>>() {
            @Override
            public int compare(Pair<SearchNode, SearchNode> p1, Pair<SearchNode, SearchNode> p2) {
                int r = -getLookaheadLeafHeuristic(p1.getRight()) + getLookaheadLeafHeuristic(p2.getRight());
                if (r==0) return -p1.getLeft().heuristicScore() + p2.getLeft().heuristicScore();
                return r;
            }
        });
        return leve1WithBestLookahead.stream().map(pair -> pair.getLeft()).collect(Collectors.toList());
    }
    

    private SearchNode lookAheadSelection(List<SearchNode> toExplore, List<Integer> branchingFactors) {
        if (branchingFactors.size() == 0) {
            return selectBestFromLookahead(toExplore);
        }
        List<SearchNode> nextLevel = new ArrayList<>();
        for (SearchNode n : toExplore) {
            if (n == null) continue;
            BellmanFordBoardExplorer boardExplorer = new BellmanFordBoardExplorer(n);
            List<SearchNode> reachable = boardExplorer.computeReachableFinalNodesWithScores();
            List<SearchNode> nodes = selectNodes(reachable, branchingFactors.get(0));
            nextLevel.addAll(nodes);
        }
        if (nextLevel.size() == 0) {
            return selectBestFromLookahead(toExplore);
        }
        return lookAheadSelection(nextLevel, branchingFactors.subList(1, branchingFactors.size()));
    }
    
    private SearchNode selectBestFromLookahead(List<SearchNode> toExplore) {
        SearchNode bestNode = null;
        int bestScore = Integer.MIN_VALUE;
        for (SearchNode n4 : toExplore) {
            if (n4 == null) continue;
            if (getLookaheadLeafHeuristic(n4) > bestScore) {
                bestScore = getLookaheadLeafHeuristic(n4);
                bestNode = n4;
            }
        }
        return bestNode;
    }
    
    private int getLookaheadLeafHeuristic(SearchNode n) {
        return n.getScore();
    }

    private List<SearchNode> selectNodes(Collection<SearchNode> reachable, int count) {
        List<SearchNode> representatives = new ArrayList<>(reachable);
        Collections.shuffle(representatives, cfg.rnd);
        if (count == -1) return representatives;
        int countNeeded = Math.min(count, representatives.size());
        return selectBestN(representatives, countNeeded);
    }
    
    private List<SearchNode> selectBestN(List<SearchNode> representatives, int countNeeded) {
        if (countNeeded == 1)
            return Collections.singletonList(selectSingleBest(representatives));
        representatives.sort(Comparator.comparing(SearchNode::heuristicScore).reversed());
        return representatives.subList(0, Math.min(countNeeded, representatives.size()));
    }

    private SearchNode selectSingleBest(List<SearchNode> representatives) {
        SearchNode best = representatives.get(0);
        int bestScore = best.heuristicScore();
        for (SearchNode searchNode : representatives) {
            if (searchNode.heuristicScore() > bestScore) {
                best = searchNode;
                bestScore = best.heuristicScore();
            }
        }
        return best;
    }

    private boolean stopCondition() {
        return queue.isEmpty() || (cfg.timeLimitSeconds > 0 && ((System.currentTimeMillis() - startTime) > cfg.timeLimitSeconds*1000));
    }
}
