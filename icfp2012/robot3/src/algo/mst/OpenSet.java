package algo.mst;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import model.Mine;

class OpenSet {
	private final Map<Mine, SearchNode> scoreMap = new HashMap<Mine, SearchNode>();
	private final PriorityQueue<SearchNode> queue = new PriorityQueue<SearchNode>();
	
	public void add(SearchNode square) {
		scoreMap.put(square.getMine(), square);
		queue.add(square);
	}

	public void replace(SearchNode olds, SearchNode news) {
		if (olds != null) queue.remove(olds);
		add(news);
	}

	public SearchNode get(Mine mine) {
		return scoreMap.get(mine);
	}

	public SearchNode head() {
		SearchNode result = queue.poll();
		scoreMap.remove(result.getMine());
		return result;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	
}