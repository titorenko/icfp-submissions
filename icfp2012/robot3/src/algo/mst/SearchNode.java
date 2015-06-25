package algo.mst;

import algo.simple.heuristics.Delta;
import model.Mine;


class SearchNode implements Comparable<SearchNode>{
	int gScore = 0;
	Delta hScore = Delta.NULL;
	private Mine mine;
	
	SearchNode(Mine mine, int gScore, Delta delta) {
		this.mine = mine;
		this.gScore = gScore;
		this.hScore = delta;
	}
	
	
	public int getGScore() {
		return gScore;
	}
	
	public Delta getHScore() {
		return hScore;
	}
	
	public int getFScore() {
		return hScore.getValue()+gScore;
	}

	@Override
	public int compareTo(SearchNode other) {
		return other.getFScore() - getFScore();
	}


	@Override
	public String toString() {
		return "Node [gScore=" + gScore + ", hScore=" + hScore + "]";
	}


	public Mine getMine() {
		return mine;
	}


	public int getIndex() {
		return mine.getRobotCell();
	}

	
}