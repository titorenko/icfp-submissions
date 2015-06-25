package model;

public enum RobotState {
	ABORTED, NORMAL, WINNING, DROWNED, CRUSHED;

	public boolean isFinished() {
		return this != NORMAL;
	}
	
	public boolean isDead() {
		return this == CRUSHED || this == DROWNED;
	}

}
