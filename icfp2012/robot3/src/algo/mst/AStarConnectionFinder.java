package algo.mst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Mine;
import model.Move;
import algo.simple.heuristics.Delta;
import algo.simple.heuristics.HeuristicEngineContext;
import algo.simple.heuristics.HeuristicalEngine;

public class AStarConnectionFinder {
	
	private OpenSet openSet;
	private Set<Mine> closedSet;

	private HeuristicEngineContext hctx;

	public List<Edge> findConnections(Mine mine, List<Integer> targetIds) {
		List<Edge> result = new ArrayList<Edge>();
		for (Integer target : targetIds) {
			Edge edge = findConnection(mine, target);
			if (edge == null) {
				continue;
			}
			result.add(edge);
		}
		return result;
	}
	
	private SearchNode init(Mine mine, HeuristicalEngine engine) {
		this.hctx = new HeuristicEngineContext();
		this.openSet = new OpenSet();
		SearchNode start = new SearchNode(mine, 0, Delta.NULL);
		openSet.add(start);
		engine.applyDynamicHeuristics(mine, hctx);
		hctx.setFollowUpEvalMode();
		this.closedSet = new HashSet<Mine>();
		return start;
	}
	
	Edge findConnection(Mine mine, Integer target) {
		return findConnection(mine, target, HeuristicalEngineFactory.getOptimisticEngineForAStarEval(mine, target), 5000);
	}
	
	public Edge findConnection(Mine mine, Integer target, HeuristicalEngine engine, int timeout) {
		long start = System.currentTimeMillis(); 
		SearchNode startNode = init(mine, engine);
		
		while(!openSet.isEmpty() && (System.currentTimeMillis() - start) < timeout) {
			SearchNode current = openSet.head();
			if (current.getMine().getRobotCell() == target)  {
				return new Edge(startNode, current);
			}
			boolean added = closedSet.add(current.getMine());
			if (!added) continue;
			List<Move> possibleMoves = current.getMine().getPossibleRobotMoves();
			for (Move move : possibleMoves) {
				Mine newMine = current.getMine().makeMove(move);
				if (newMine.getState().isDead()) continue;
				if (closedSet.contains(newMine)) continue;
				Delta delta = engine.applyHeuristics(newMine, hctx);
				//System.out.println(newMine);
				SearchNode neighbor = new SearchNode(newMine, current.gScore - 1, delta);
				SearchNode visited = openSet.get(newMine);
				if (visited == null || visited.gScore < neighbor.gScore) {
					openSet.replace(visited, neighbor);
				}
			}
		}
		return null;
	}

}