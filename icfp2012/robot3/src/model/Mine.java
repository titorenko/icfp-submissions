package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class Mine {

	private final MineConfiguration cfg;
	
	private final byte[] cells;
	
	private Robot robot = new Robot(-1, -1, -1);
	private int liftCellIndex = -1;
	
	private final int initialAllLambdaCount;
	private final int initialLambdaCount;
	private List<Integer> lambdaCells = new ArrayList<Integer>();
	private int higherOrderLambaCount = 0;
	
	final List<Integer> movingObstacles = new ArrayList<Integer>();

	private RobotState state = RobotState.NORMAL;
	private FloodingState floodingState = FloodingState.NO_FLOODING;
	private TrampolineState trampolineState;
	private BeardState beardState = BeardState.NULL;

	
	public Mine(int width, Cell... cells) {
		this(new MineConfiguration(width, cells.length / width, new ElementsConfig()), Cell.encode(cells));
	}
	
	public Mine(int width, ElementsConfig ec, Cell... cells) {
		this(new MineConfiguration(width, cells.length / width, ec), Cell.encode(cells));
		this.trampolineState = new TrampolineState(this, ec.trampolines);
		this.floodingState = cfg.getInitialFloodingState();
		this.beardState = new BeardState(this);
		if (beardState.getBeardCount() == 0) this.beardState = BeardState.NULL;
	}

	public Mine(MineConfiguration cfg, byte[] cells) {
		this.cfg = cfg;
		this.cells = cells;
		for (int idx=0; idx<cells.length; idx++) {
			if (cells[idx] == Cell.CLOSED_LIFT.getEncoding()) {
				this.liftCellIndex = idx;
			} else if (cells[idx] == Cell.ROBOT.getEncoding()) {
				int rx = idx % cfg.getWidth();
				int ry = idx / cfg.getWidth();
				this.robot = new Robot(rx, ry, cfg.initialRazors);
			} else if (cells[idx] == Cell.LAMBDA.getEncoding()) {
				this.lambdaCells.add(idx);
			} else if (cells[idx] == Cell.HO_LAMBDA.getEncoding()) {
				this.higherOrderLambaCount++;
			}
		}
		this.initialLambdaCount = lambdaCells.size();
		this.initialAllLambdaCount = higherOrderLambaCount + lambdaCells.size();
	}
	
	Mine(Mine previous, Robot newRobot) {
		this(previous, newRobot, previous.lambdaCells, previous.trampolineState);
	}
	
	Mine(Mine previous, Robot newRobot, List<Integer> lambdaCells, TrampolineState trampolineState) {
		this.cfg = previous.cfg;
		this.cells = Arrays.copyOf(previous.cells, previous.cells.length);
		this.robot = newRobot;
		this.liftCellIndex = previous.liftCellIndex;
		this.lambdaCells = new ArrayList<Integer>(lambdaCells);
		this.floodingState = previous.floodingState;
		this.trampolineState = trampolineState;
		this.beardState =  previous.beardState;
		this.initialAllLambdaCount = previous.initialAllLambdaCount;
		this.initialLambdaCount = previous.initialLambdaCount;
	}

	
	public Mine removeRobot() {
		int robotCellIdx = getRobotCell();
		Mine mine = new Mine(this, new Robot(robot, Move.W, -1, -1, false, 0));
		mine.setUnsafe(robotCellIdx, Cell.EMPTY);
		return mine;
	}

	public Mine makeMove(Move move) {
		Mine mineAfterRobotMove = robot.makeMove(this, move);
		Mine newMine = mineAfterRobotMove.update();
		newMine.movingObstacles.addAll(mineAfterRobotMove.movingObstacles);
		newMine.floodingState = floodingState.next(mineAfterRobotMove);
		newMine.beardState = newMine.beardState.next(newMine);
		newMine.state = newMine.evalRobotState(move, this);
		return newMine;
	}
	
	public Mine makeNullMove() {
		return update();
	}
	
	public Mine makeMoves(Path path) {
		Mine result = this;
		for (Move move : path) {
			result = result.makeMove(move);
			if (result.getState().isFinished()) {
				return result;
			}
		}
		return result;
	}
	
	public int getScore() {
		return robot.getScore(state);
	}
	
	public List<Integer> getLambdaCells() {
		return lambdaCells;
	}
	
	public List<Integer> getHoLambdaCells() {
		return findCells(Cell.HO_LAMBDA);
	}
	
	public int getHigherOrderLambaCount() {
		return higherOrderLambaCount;
	}
	
	public int getInitialAllLambdaCount() {
		return initialAllLambdaCount;
	}
	
	public int getInitialLambdaCount() {
		return initialLambdaCount;
	}
	
	public int getUncollectedLambdaCount() {
		return initialAllLambdaCount - robot.getLambdaCollected();
	}
	
	public List<Integer> getMovingObstacles() {
		return movingObstacles;
	}
	
	public int getRobotCell() {
		return robot.getY() * getWidth() + robot.getX();
	}
	
	public int getLiftCell() {
		return liftCellIndex;
	}
	
	private RobotState evalRobotState(Move lastMove, Mine oldMine) {
		int robotCellIndex = index(robot.getX(), robot.getY());
		
        if (robotCellIndex == liftCellIndex) {
            return RobotState.WINNING;
        }
        if (lastMove == Move.A) {
            return RobotState.ABORTED;
        }
        if (get(robot.getX(), robot.getY()+1).isHardFallingThing()) {
        	boolean isSameOnMoveBefore = oldMine.get(robot.getX(), robot.getY()+1).isHardFallingThing();
            if (!isSameOnMoveBefore) {
                return RobotState.CRUSHED;
            }
        }
        if (floodingState.getUnderwaterTime() > cfg.getWaterproof()) {
        	return RobotState.DROWNED;
        }
        return RobotState.NORMAL;
	}
	
	Mine update() {
		Mine newMine = new Mine(this, robot);
		int width = getWidth();
		byte[] cells = this.cells;
		for (int idx = 0; idx < cells.length; idx++) {
			Cell cell = Cell.fromEncoding(cells[idx]);
        	if (cell.isHardFallingThing()) {
        		int x = idx % width;
        		int y = idx / width;
        		updateFalling(cell, x, y, newMine);
        	} else if (cell == Cell.CLOSED_LIFT && isAllLambdasCollected()) {
        		newMine.setUnsafe(idx, Cell.OPEN_LIFT);
        	} else if (cell == Cell.BEARD && getBeardState().isBeardGrowthTime()) {
        		int x = idx % width;
        		int y = idx / width;
        		newMine.movingObstacles.add(idx);
        		growBeard(newMine, x, y);
        	}
		}
		return newMine;
	}

	private boolean isAllLambdasCollected() {
		return initialAllLambdaCount == robot.getLambdaCollected();
	}

	private void growBeard(Mine newMine, int x, int y) {
		int nGrown = 0;
		for(int dy=-1; dy <= 1; dy++) {
            for(int dx=-1; dx<=1; dx++) {
                if (get(x + dx, y + dy) == Cell.EMPTY) {
                	newMine.set(x + dx, y + dy, Cell.BEARD);
                	newMine.movingObstacles.add(index(x + dx, y + dy));
                	nGrown++;
                }
            }
        }
		newMine.beardState = newMine.beardState.beardsGrowth(nGrown);
	}

	private void updateFalling(Cell cell, int x, int y, Mine newMine) {
		Cell below = get(x, y-1);
		if (below == Cell.EMPTY) {
			landRock(cell, x, y,  below, x, y-1, newMine);
		} else if (below.isHardFallingThing()) { //roll from the rock
			if (get(x+1, y) == Cell.EMPTY && get(x+1, y-1) == Cell.EMPTY) { //fall left
				landRock(cell, x, y, get(x+1, y-1), x+1, y-1, newMine);
			} else if (get(x-1, y) == Cell.EMPTY && get(x-1, y-1) == Cell.EMPTY) { //fall right
				landRock(cell, x, y, get(x-1, y-1), x-1, y-1, newMine);
			}
		} else if (below == Cell.LAMBDA && get(x+1, y) == Cell.EMPTY && get(x+1, y-1) == Cell.EMPTY) {
			landRock(cell, x, y, get(x+1, y-1), x+1, y-1, newMine);
		}
	}
	

	private void landRock(Cell fallingCell, int fx, int fy, Cell landingTo, int lx, int ly, Mine newMine) {
    	newMine.set(fx, fy, Cell.EMPTY);
    	int idx = index(lx, ly);
        if (fallingCell == Cell.HO_LAMBDA && newMine.isNonEmptyAndWithoutRobot(lx, ly-1)) {
        	newMine.set(lx, ly, Cell.LAMBDA);
        	newMine.lambdaCells.add(index(lx, ly));
        } else {
        	if (newMine.unsafeGet(idx) == Cell.LAMBDA) newMine.lambdaCells.remove((Integer)idx);
        	newMine.setUnsafe(idx, fallingCell);
        	newMine.movingObstacles.add(idx);
        }
    }

	private boolean isNonEmptyAndWithoutRobot(int x, int y) {
		return (get(x, y) != Cell.EMPTY && index(x, y) != getRobotCell()); //do not convert to lambda if we are about to die
	}
    
    public List<Move> getPossibleRobotMoves() {
    	return getPossibleMoves(robot.getX(), robot.getY());
    }
    
    public List<Move> getPossibleMoves(int x, int y) {
    	List<Move> moves = new ArrayList<Move>(5);
    	if (canShaveFrom(x,y)) {
    		moves.add(Move.S);
    	}
		if (get(x, y+1).canEasilyMoveInto()) {
			moves.add(Move.U);
		}
		if (get(x, y-1).canEasilyMoveInto()) {
			moves.add(Move.D);
		}
		Cell left = get(x-1, y);
		if (left.canEasilyMoveInto() || (left.isHardFallingThing() && get(x-2, y) == Cell.EMPTY)) {
			moves.add(Move.L);
		}
		Cell right = get(x+1, y);
		if (right.canEasilyMoveInto() || (right.isHardFallingThing() && get(x+2, y) == Cell.EMPTY)) {
			moves.add(Move.R);
		}
		moves.add(Move.W);
		return moves;
	}
    
    private boolean canShaveFrom(int x, int y) {
    	if (beardState.getBeardCount() <= 0 || robot.getRazorCount() <= 0) return false;
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				if (get(dx + x, dy + y) == Cell.BEARD) return true; 
			}
		}
		return false;
	}


    public void set(int x, int y, Cell cell) {
    	setUnsafe(index(x, y), cell);
	}
    
    public void setUnsafe(int idx, Cell cell) {
		cells[idx] = cell.getEncoding();
	}
    
    void setUnsafe(List<Integer> indexes, Cell cell) {
    	for (Integer idx : indexes) {
    		cells[idx] = cell.getEncoding();	
		}
	}

	public Cell get(int x, int y) {
		if (x<0 || x>=getWidth() || y<0 || y>=getHeight()) return Cell.NULL;
		return unsafeGet(index(x, y));
	}
	
	public Cell unsafeGet(int idx) {
		return Cell.fromEncoding(cells[idx]);
	}

	public int getWidth() {
		return cfg.getWidth();
	}

	public int getHeight() {
		return cfg.getHeight();
	}
	
	public Robot getRobot() {
		return robot;		
	}
	
	public RobotState getState() {
		return state;
	}

	public int index(int x, int y) {
		return y * getWidth() + x;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				result.append((char)cells[index(x, y)]);
			}
			if (y != 0)
				result.append("\n");
		}
		if (getCfg().isFloodingActive()) {
			result.append("\nFlood level "+getFloodingState().getWaterLevel()+" under water "+getFloodingState().getUnderwaterTime());
		}
		return result.toString();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(cells);
	}
	
	@Override
	public boolean equals(Object obj) {
		Mine other = (Mine) obj;
		return Arrays.equals(cells, other.cells);
	}

	public boolean isRobotUnderWater() {
		return robot.getY() <= floodingState.getWaterLevel();
	}
	
	public FloodingState getFloodingState() {
		return floodingState;
	}
	
	public TrampolineState getTrampolineState() {
		return trampolineState;
	}
	
	public MineConfiguration getCfg() {
		return cfg;
	}

	public int findCell(Cell toFind) {
		for (int idx = 0; idx < cells.length; idx++) {
			if (cells[idx] == toFind.getEncoding()) return idx;
		}
		return -1;
	}
	
	public List<Integer> findCells(Cell toFind) {
		List<Integer> result = new ArrayList<Integer>();
		for (int idx = 0; idx < cells.length; idx++) {
			if (cells[idx] == toFind.getEncoding()) result.add(idx);
		}
		return result;
	}
	
	public int getCellCount(Cell cellType) {
		int result = 0;
		for (byte cell : cells) {
			if (cell == cellType.getEncoding()) result++;
		}
		return result;
	}

	public int getTarget(int index) {
		return trampolineState.getTarget(index);
	}

	public List<Integer> getSources(int index) {
		return trampolineState.getSources(index);
	}

	public BeardState getBeardState() {
		return beardState;
	}

	public void setBeardState(BeardState beardState) {
		this.beardState = beardState;
	}
	
	public int distanceTo(int x, int y, int otherX, int otherY) {
		return Math.abs(x-otherX)+Math.abs(y-otherY);
	}

	public Mine clone() {
		return new Mine(this, robot);
	}

	public Coordinate toCoord(Integer candidate) {
		int x = candidate % getWidth();
		int y = candidate / getWidth();
		return new Coordinate(x, y);
	}

	public boolean hasRocksOrLambdasFallen() {
		for (Integer idx : getMovingObstacles()) {
			if (unsafeGet(idx).isHardFallingThing()) return true;
		}
		return false;
	}

}