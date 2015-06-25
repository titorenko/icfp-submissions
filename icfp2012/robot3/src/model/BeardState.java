package model;

public class BeardState {
	
	public static final BeardState NULL = new BeardState(-1, -1) {
		public BeardState next(Mine mine) {
			return NULL;
		}
		
		public boolean isBeardGrowthTime() {
			return false;
		}
		
		public boolean isActive() {
			return false;
		}
	};
	
	private final int timeToGrowBeard;
	private final int beardCount;
	
	public BeardState(Mine mine) {
		this.timeToGrowBeard = mine.getCfg().growthRate - 1;
		this.beardCount = mine.getCellCount(Cell.BEARD);
	}

	private BeardState(int timeToGrowBeard, int beardCount) {
		this.timeToGrowBeard = timeToGrowBeard;
		this.beardCount = beardCount;
	}

	public BeardState next(Mine mine) {
		if (timeToGrowBeard == 0) {
			return new BeardState(mine.getCfg().growthRate - 1, beardCount);
		} else {
			return new BeardState(timeToGrowBeard - 1, beardCount);
		}
	}

	public boolean isBeardGrowthTime() {
		return timeToGrowBeard == 0;
	}

	public int getBeardCount() {
		return beardCount;
	}

	public BeardState beardsGrowth(int nGrown) {
		return new BeardState(timeToGrowBeard, beardCount + nGrown);
	}
	
	public BeardState beardsShaved(int nShaved) {
		return new BeardState(timeToGrowBeard, beardCount - nShaved);
	}
	
	public boolean isActive() {
		return true;
	}
	
	@Override
	public String toString() {
		return "BeardState [timeToGrowBeard=" + timeToGrowBeard
				+ ", beardCount=" + beardCount + "]";
	}

}
