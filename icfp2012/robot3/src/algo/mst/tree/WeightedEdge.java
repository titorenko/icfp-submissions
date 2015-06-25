package algo.mst.tree;

import model.Mine;
import algo.mst.Edge;

public class WeightedEdge implements Comparable<WeightedEdge> {
	private Edge edge;
	private int weight;

	WeightedEdge(Edge edge, int weight) {
		this.edge = edge;
		this.weight = weight;
	}
	
	public Mine getEndMine() {
		return edge.getEndMine();
	}

	@Override
	public int compareTo(WeightedEdge other) {
		return other.weight - weight;
	}

	@Override
	public String toString() {
		return "WeightedEdge [edge=" + edge + ", weight=" + weight + "]";
	}

	public int getWeight() {
		return weight;
	}

}