package model;

import java.util.ArrayList;
import java.util.List;



public class Robot {
	private final Path path;
	private final int x;
	private final int y;

	private final int score;
	private final int lambdaCollected;
	private final int razorCount;
	private boolean hasJumped = false;

	Robot(int x, int y, int razorCount) {
		this.path = new Path();
		this.lambdaCollected = 0;
		this.x = x;
		this.y = y;
		this.score = 0;
		this.razorCount = razorCount;
	}

	public Robot(Robot robot, Move move, int newX, int newY, boolean hadCollectedLambda, int razorCount) {
		this.path = robot.path.addMove(move);
		this.lambdaCollected = hadCollectedLambda ? robot.lambdaCollected + 1 : robot.lambdaCollected;
		this.razorCount = razorCount;
		this.x = newX;
		this.y = newY;
		if (move == Move.A) {
			this.score = robot.score;
		} else {
			this.score = hadCollectedLambda ? robot.score + 24 : robot.score - 1;
		}
	}

	public Mine makeMove(final Mine mine, final Move move) {
		int newX = move.newX(x);
		int newY = move.newY(y);
		int newIndex = mine.index(newX, newY);
		Cell newRobotCell = mine.get(newX, newY);
		if (newRobotCell.canEasilyMoveInto()) {
			if (newRobotCell == Cell.LAMBDA) {
				return createNewMine(mine, move, newX, newY, newIndex);
			} else if (newRobotCell.isTrampoline()) {
				int target = mine.getTarget(newIndex);
				TrampolineState trampolineState = mine.getTrampolineState().newStateAfterJump(newIndex, target);
				Mine newMine = createNewMine(mine, move, target % mine.getWidth(), target / mine.getWidth(), trampolineState, razorCount);
				newMine.setUnsafe(newIndex, Cell.EMPTY);
				newMine.setUnsafe(mine.getSources(target), Cell.EMPTY);
				newMine.getRobot().setJumped(true);
				return newMine;
			} else if (newRobotCell == Cell.RAZOR) {
				return createNewMine(mine, move, newX, newY, mine.getTrampolineState(), razorCount+1);
			} else {
				return createNewMine(mine, move, newX, newY);
			}
		} else if (newRobotCell.isHardFallingThing()) {
			if (move == Move.L && mine.get(newX-1, newY) == Cell.EMPTY) {
				Mine newMine = createNewMine(mine, move, newX, newY);
				newMine.set(newX-1, newY, newRobotCell);
				newMine.movingObstacles.add(newIndex-1);
				return newMine;
			} else if (move == Move.R && mine.get(newX+1, newY) == Cell.EMPTY) {
				Mine newMine = createNewMine(mine, move, newX, newY);
				newMine.set(newX+1, newY, newRobotCell);
				newMine.movingObstacles.add(newIndex+1);
				return newMine;
			}
		} else if (move == Move.S && razorCount > 0) {
			Mine newMine =  createNewMine(mine, move, x, y, mine.getTrampolineState(), razorCount-1);
			newMine.movingObstacles.add(newIndex);
			int nShaved = 0;
			for (int dy = -1; dy <= 1; dy++) {
				for (int dx = -1; dx <= 1; dx++) {
					if (mine.get(dx + x, dy + y) == Cell.BEARD) {
						newMine.set(dx + x, dy + y, Cell.EMPTY);
						nShaved++;
					}
				}
			}
			newMine.setBeardState(newMine.getBeardState().beardsShaved(nShaved));
			return newMine;
		}
		return new Mine(mine, new Robot(this, move, x, y, false, razorCount));
	}
	
	public boolean hasJumped() {
		return hasJumped;
	}
	
	private void setJumped(boolean hasJumped) {
		this.hasJumped = hasJumped;
	}

	private Mine createNewMine(Mine mine, Move move, int newX, int newY, int collectedLambdaIndex) {
		Robot newRobot = new Robot(this, move, newX, newY, true, razorCount);
		List<Integer> lambdaCells = new ArrayList<Integer>(mine.getLambdaCells());
		lambdaCells.remove(lambdaCells.indexOf(collectedLambdaIndex));
		Mine newMine = new Mine(mine, newRobot, lambdaCells, mine.getTrampolineState());
		newMine.set(x, y, Cell.EMPTY);
		newMine.set(newX, newY, Cell.ROBOT);
		return newMine;
	}
	
	private Mine createNewMine(Mine mine, Move move, int newX, int newY) {
		return createNewMine(mine, move, newX, newY, mine.getTrampolineState(), razorCount);
	}
	
	private Mine createNewMine(Mine mine, Move move, int newX, int newY, TrampolineState trampolineState, int razorCount) {
		Robot newRobot = new Robot(this, move, newX, newY, false, razorCount);
		Mine newMine = new Mine(mine, newRobot, mine.getLambdaCells(), trampolineState);
		newMine.set(x, y, Cell.EMPTY);
		newMine.set(newX, newY, Cell.ROBOT);
		return newMine;
	}

	private int getMiningCompeleteScore() {
		return score + lambdaCollected*50;
	}
	
	private int getMiningAbortedScore() {
		return score+lambdaCollected*25;
	}
	
	public int getScore(RobotState state) {
		switch (state) {
		case WINNING:
			return getMiningCompeleteScore();
		case ABORTED:
		case NORMAL:
			return getMiningAbortedScore();
		default:
			return score;
		}
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Path getPath() {
		return path;
	}

	public int getLambdaCollected() {
		return lambdaCollected;
	}
	
	public int getRazorCount() {
		return razorCount;
	}

	public int distanceTo(int otherX, int otherY) {
		return Math.abs(x-otherX)+Math.abs(y-otherY);
	}
	
	@Override
	public String toString() {
		return "Robot with score "+score+" and path "+path;
	}

	
}