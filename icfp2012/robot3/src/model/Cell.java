package model;


public enum Cell {
	
	EMPTY(' ', false, true, false), 
	EARTH('.', false, true, false), 
	WALL('#', false, false, false), 
	LAMBDA('\\', false, true, false), 
	ROCK('*', true, false, false),
	
	ROBOT('R', false, false, false), 
	
	OPEN_LIFT('O', false, true, true), 
	CLOSED_LIFT('L', false, false, true), 
	
	TRAMPOLINE_A('A', false, true, false, false, true), 
	TRAMPOLINE_B('B', false, true, false, false, true), 
	TRAMPOLINE_C('C', false, true, false, false, true), 
	TRAMPOLINE_D('D', false, true, false, false, true), 
	TRAMPOLINE_E('E', false, true, false, false, true), 
	TRAMPOLINE_F('F', false, true, false, false, true), 
	TRAMPOLINE_G('G', false, true, false, false, true), 
	TRAMPOLINE_H('H', false, true, false, false, true), 
	TRAMPOLINE_I('I', false, true, false, false, true), 
	
	TARGET_1('1', false, false, false, true, false),
	TARGET_2('2', false, false, false, true, false),
	TARGET_3('3', false, false, false, true, false),
	TARGET_4('4', false, false, false, true, false),
	TARGET_5('5', false, false, false, true, false),
	TARGET_6('6', false, false, false, true, false),
	TARGET_7('7', false, false, false, true, false),
	TARGET_8('8', false, false, false, true, false),
	TARGET_9('9', false, false, false, true, false),
	
	BEARD('W', false, false, false),
	RAZOR('!', false, true, false),
	
	HO_LAMBDA('@', true, false, false), 
	
	NULL('?', false, false, false);
	
	private final byte encoding;
	private final boolean isHardFallingThing;
	private final boolean canEasilyMoveInto;
	private final boolean isLift;
	private final boolean isTarget;
	private final boolean isTrampoline;

	Cell(char encoding, boolean isHardFallingThing, boolean canEasilyMoveInto, boolean isLift) {
		this(encoding, isHardFallingThing, canEasilyMoveInto, isLift, false, false);
	}
	
	Cell(char encoding, boolean isHardFallingThing, boolean canEasilyMoveInto, boolean isLift, boolean isTarget, boolean isTrampoline) {
		this.encoding = (byte) encoding;
		this.isHardFallingThing = isHardFallingThing;
		this.canEasilyMoveInto = canEasilyMoveInto;
		this.isLift = isLift;
		this.isTarget = isTarget;
		this.isTrampoline = isTrampoline;
	}
	
	public boolean isHardFallingThing() {
		return isHardFallingThing;
	}
	
	public boolean canEasilyMoveInto() {
		return canEasilyMoveInto;
	}
	
	public boolean isLift() {
		return isLift;
	}

	byte getEncoding() {
		return encoding;
	}
	
	public static Cell fromEncoding(byte encoding) {
		return CellLookup.prototypes[encoding];
	}
	
	@Override
	public String toString() {
		return Character.toString((char)getEncoding());
	}

	static byte[] encode(Cell[] cells) {
		byte[] result = new byte[cells.length];
		for (int i=0; i<cells.length; i++) {
			result[i] = cells[i].encoding;
		}
		return result;
	}

	public boolean isTarget() {
		return isTarget;
	}

	public boolean isTrampoline() {
		return isTrampoline;
	}

	public boolean canMoveInto() {
		return canEasilyMoveInto || isHardFallingThing;
	}

	public boolean isHardBarier() {
		return this == NULL || this == WALL;
	}
}

class CellLookup {
	static final Cell[] prototypes = new Cell[getMaxCellEncoding()+1];
	
	static {
		for(Cell cell : Cell.values()) {
			prototypes[cell.getEncoding()] = cell;
		}
	}

	private static int getMaxCellEncoding() {
		int max = Cell.NULL.getEncoding();
		for(Cell cell : Cell.values()) {
			if (cell.getEncoding() > max) {
				max = cell.getEncoding();
			}
		}
		return max;
	}
}