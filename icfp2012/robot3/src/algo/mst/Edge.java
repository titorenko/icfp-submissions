package algo.mst;

import model.Mine;
import model.Path;

public class Edge {
	private final SearchNode startNode;
	private final SearchNode endNode;
	
	public Edge(SearchNode startNode, SearchNode endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
	}
	
	public int getLength() {
		return Math.abs(endNode.gScore);
	}

	public Path getEndPath() {
		return endNode.getMine().getRobot().getPath();
	}

	@Override
	public String toString() {
		Path start = startNode.getMine().getRobot().getPath();
		Path end = endNode.getMine().getRobot().getPath();
		Path delta = end.subpath(start.length());
		return "Edge "+delta+" "+startNode.getIndex()+" -> "+endNode.getIndex()+ " ("+endNode.getFScore()+") ";
	}

	public Mine getEndMine() {
		return endNode.getMine();
	}

	
	
	
}