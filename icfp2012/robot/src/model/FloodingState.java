package model;


public class FloodingState {
    public static final FloodingState NO_FLOODING = new FloodingState(-1, 0, Integer.MAX_VALUE) {
    	public FloodingState next(Mine mine) {
    		return NO_FLOODING;
    	}
    };
	
    private final int waterLevel;
    private final int underWaterTime;
    private final int timeToNextBump;

    public FloodingState(int waterLevel, int underwaterTime, int timeToNextBump) {
        this.waterLevel = waterLevel;
        this.underWaterTime = underwaterTime;
        this.timeToNextBump = timeToNextBump;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public int getUnderwaterTime() {
        return underWaterTime;
    }

    public int getTimeToNextBump() {
        return timeToNextBump;
    }

	public FloodingState next(Mine mine) {
		int underWaterTime = mine.isRobotUnderWater() ? this.underWaterTime+1 : 0; 
		if (timeToNextBump == 1) {
			return new FloodingState(waterLevel+1, underWaterTime, mine.getCfg().getFloodingRate());
		} else {
			return new FloodingState(waterLevel, underWaterTime, timeToNextBump-1);
		}
	}
	
}
