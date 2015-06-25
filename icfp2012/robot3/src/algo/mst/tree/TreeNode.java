package algo.mst.tree;

import java.util.ArrayList;
import java.util.List;

import model.Mine;
import model.Path;
import model.RobotState;
import algo.mst.AStarConnectionFinder;
import algo.mst.CandidateFinder;
import algo.mst.Edge;
import algo.mst.HeuristicalEngineFactory;
import algo.simple.heuristics.Delta;
import algo.simple.heuristics.HeuristicalEngine;

public class TreeNode {
	private Mine mine;
	private TreeNode parent;
	private List<TreeNode> children;
	
	private List<Integer> candidates;
	private int candidateIndex;
	private int score;

	public TreeNode(WeightedEdge edge, TreeNode parent) {
		this(edge.getEndMine(), edge.getWeight(), parent);
	}
	
	public TreeNode(Mine mine, int score, TreeNode parent) {
		this.mine = mine;
		this.score = score;
		this.parent = parent;
		this.children = new ArrayList<TreeNode>();
	}

	public Path getPathDelta() {
		if (parent == null)
			return getPath();
		return getPath().subpath(parent.getPath().length());
	}

	private Path getPath() {
		return mine.getRobot().getPath();
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public Mine getMine() {
		return mine;
	}

	public TreeNode nextChild() {
		if (candidates == null) initNextEdgesSearch();
		if (candidateIndex >= candidates.size()) return null;
		do {
			WeightedEdge edge = findNextEdge(candidates.get(candidateIndex));
			candidateIndex++;
			if (edge != null) {
				TreeNode child = new TreeNode(edge, this);
				children.add(child);
				return child;
			}
		} while (candidateIndex < candidates.size());
		return null;
	}
	
	public boolean hasMoreChildren() {
		if (candidates == null) initNextEdgesSearch();
		return (candidateIndex < candidates.size());
	}
	
	public int getScore() {
		return score;
	}

	private void initNextEdgesSearch() {
		CandidateFinder finder = new CandidateFinder();
		this.candidates = finder.getCandidates(mine);
		this.candidateIndex = 0;
	}
	
	private WeightedEdge findNextEdge(int candidate) {
		AStarConnectionFinder finder = new AStarConnectionFinder();
		HeuristicalEngine oEngine = HeuristicalEngineFactory.getOptimisticEngineForAStarEval(mine, candidate);
		Edge connection = finder.findConnection(mine, candidate, oEngine, 5000);
		if (connection == null) return null;//TODO search for razors, trampolines, ho here
		int evaluation = evaluate(connection);
		if (evaluation < -100000) {
			HeuristicalEngine pEngine = HeuristicalEngineFactory.getPessimisticEngineForAStarEval(mine, candidate);
			connection = finder.findConnection(mine, candidate, pEngine, 10000);
			if (connection == null) return null; 
			evaluation = evaluate(connection);
		}
		if (evaluation < -100000) return null; 
		return new WeightedEdge(connection, evaluation);
	}
	
	private int evaluate(Edge edge) {
		Mine endMine = edge.getEndMine();
		if (endMine.getState() == RobotState.WINNING) return 1000000;
		HeuristicalEngine engine = HeuristicalEngineFactory.getEngine(endMine);
		Delta delta = engine.applyHeuristics(endMine);
		int score = endMine.getScore();
		if (endMine.getState().isDead() || delta.isDeadEnd()) return -1000000;
		return score+delta.getValue();
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return mine.getRobot().getPath().toString();
	}

}