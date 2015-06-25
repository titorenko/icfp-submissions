package algo.simple.heuristics;

public class Delta implements Comparable<Delta> {
	public static final Delta MAX_VALUE = new Delta(1000000, false);
	public static final Delta MIN_VALUE = new Delta(-1000000, true);
	public static final Delta NULL = new Delta(0, false);
	public static final Delta DEATH = MIN_VALUE;
	
	private final int score;
	private final boolean isDeadEnd;

	public Delta(int score, boolean isDeadEnd) {
		this.score = score;
		this.isDeadEnd = isDeadEnd;
	}
	
	public Delta(int score) {
		this.score = score;
		this.isDeadEnd = false;
	}
	
	public Delta add(Delta other) {
		return new Delta(score + other.score, isDeadEnd || other.isDeadEnd);
	}
	
	public boolean isLessThan(Delta other) {
		return compareTo(other) < 0;
	}
	
	@Override
	public int compareTo(Delta other) {
		if (isDeadEnd) {
			if(!other.isDeadEnd) return MIN_VALUE.getValue();
		} else {
			if (other.isDeadEnd) return MAX_VALUE.getValue();
		}
		return score - other.score;
	}

	@Override
	public String toString() {
		return "Delta [score=" + score + ", isDeadEnd=" + isDeadEnd + "]";
	}

	public int getValue() {
		return score;
	}

	public boolean isDeadEnd() {
		return isDeadEnd;
	}

}