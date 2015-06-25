package algo.astar;

import model.Mine;


class ScoredSquare implements Comparable<ScoredSquare>{
	int gScore = 0;
	int hScore = 0;
	private Mine mine;
	
	public ScoredSquare(Mine mine, int gScore, int hScore) {
		this.mine = mine;
		this.gScore = gScore;
		this.hScore = hScore;
	}
	
	
	public int getGScore() {
		return gScore;
	}
	
	public int getHScore() {
		return hScore;
	}
	
	public int getFScore() {
		return gScore+hScore;
	}

	@Override
	public int compareTo(ScoredSquare other) {
		return  getFScore() - other.getFScore();
	}


	@Override
	public String toString() {
		return "ScoredSquare [gScore=" + gScore + ", hScore=" + hScore + "]";
	}


	public Mine getMine() {
		return mine;
	}


	public int getIndex() {
		return mine.getRobotCell();
	}


	
}
