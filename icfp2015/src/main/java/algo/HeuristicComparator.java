package algo;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<SearchNode> {

	@Override
	public int compare(SearchNode s1, SearchNode s2) {
		return -s1.heuristicScore() + s2.heuristicScore();
	}

}
