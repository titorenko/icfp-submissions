/*package model;

import modelng.MineConfiguration;

public class Game {
    private final Robot robot;
    private final Mine mine;
	private boolean isFinished = false;

    public Game(MineConfiguration config, Cell[] cells, int width) {
        this.mine = new Mine(width, config, cells);
        this.mine.connectTrampolines(config.trampolines);
        this.robot = new Robot(config.waterproof, config.initialRazors);
    }

    public Game(Robot robot, Mine mine, boolean isFinished) {
    	this.robot = robot;
    	this.mine = mine;
    	this.isFinished = isFinished;
	}

	public Robot getRobot() {
		return robot;
	}
    
    public Mine getMine() {
        return mine;
    }
    
    public Game makeMove(Move move) {
    	Robot newRobot = robot.cloneRobotForMove(move);
		Mine newMine= mine.makeMove(newRobot, move);
		boolean isFinished = newMine.getState().isFinished();
        return new Game(newRobot, newMine, isFinished);
    }
    
    public Game fastForward() {
    	Robot newRobot = robot.cloneRobotForMove(Move.W);
		Mine newMine= mine.fastForward(newRobot);
		boolean isFinished = newMine.getState().isFinished();
        return new Game(newRobot, newMine, isFinished);
	}
    
    public Game makeMoves(Move... moves) {
    	Robot newRobot = robot.cloneRobotForMoves(moves);
    	Mine newMine = mine.makeMoves(newRobot, moves);
    	boolean isFinished = newMine.getState().isFinished();
        return new Game(newRobot, newMine, isFinished);
    }
    	
    public int getScore() {
    	return isFinished ? robot.getFinalScore() : robot.getOnAbortScore();
    }

	public boolean isFinished() {
		return isFinished;
	}
	
	@Override
	public String toString() {
		String mineState = mine.toString();
		String status = "Finished="+isFinished+", "+robot.toString();
		return mineState+"\n"+status;
	}

	public Game makeMoves(Path path) {
		return makeMoves(path.getMoves());
	}
}
*/