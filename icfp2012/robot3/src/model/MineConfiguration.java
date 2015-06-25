package model;


public class MineConfiguration {

	private final int width;
	private final int height;

	private int waterLevel = -1;
	private int floodingRate = Integer.MAX_VALUE;
	private int waterproof = 10;
	
	public int growthRate = 25;
	public int initialRazors = 0;

	public MineConfiguration(int width, int height, ElementsConfig elementsConfig) {
		this.width = width;
		this.height = height;
		this.waterLevel = elementsConfig.waterLevel;
		this.floodingRate = elementsConfig.floodingRate;
		this.waterproof = elementsConfig.waterproof;
		this.growthRate = elementsConfig.growthRate;
		this.initialRazors = elementsConfig.initialRazors;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
    
	public FloodingState getInitialFloodingState() {
		if (!isFloodingActive()) return FloodingState.NO_FLOODING;
		return new FloodingState(waterLevel, 0, floodingRate);
	}

	public int getFloodingRate() {
		return floodingRate;
	}
	
	public int getWaterproof() {
		return waterproof;
	}

	public boolean isFloodingActive() {
		return floodingRate != Integer.MAX_VALUE;
	}
	
}
