package model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import algo.IntArrayList;
import bitsets.Bitsets;

/**
 * Board should have last row always filled
 */
public class Board {
	
	private StaticBoardInfo info;

	private BitSet cells;//true means full
	
	private int sourceIndex;
	private int sourceAngle;//0..5
	private int sourceX;
	private int sourceY;

	public static Board initialBoard(BitSet cells, StaticBoardInfo info) {
		return new Board(0, 0, cells, info);
    }
	
	public Board(int sourceIndex, int sourceAngle, BitSet cells, StaticBoardInfo info) {
		this.info = info;
		this.sourceIndex = sourceIndex;
		this.sourceAngle = sourceAngle;
		this.cells = cells;
		this.sourceX  = 0;
		this.sourceY  = 0;
		if (!isFinished() && !getTemplate().canMoveTo(this, sourceAngle, sourceX, sourceY)) {
			this.sourceIndex = info.sources.size();
		}
	}
	
	public Board(int sourceIndex, int sourceAngle, BitSet cells, int sourceX, int sourceY, StaticBoardInfo info) {
		this.info = info;
		this.sourceIndex = sourceIndex;
		this.sourceAngle = sourceAngle;
		this.cells = cells;
		this.sourceX  = sourceX;
		this.sourceY  = sourceY;
	}

	public BitSet getRow(int y) {
	    if (y<0 || y>=info.height) 
	        return new BitSet();
	    return cells.get(y*info.width, (y+1)*info.width);
	}
	
	public BitSet getRow(BitSet cells, int y) {
	    if (y<0 || y>=info.height) 
	        return new BitSet();
	    return cells.get(y*info.width, (y+1)*info.width);
	}

	public boolean isSet(int x, int y) {
		return x<0 || x>=info.width || y<0 || y>=info.height || cells.get(toIndex(x, y));
    }
	
	public static boolean isSetS(int x, int y, StaticBoardInfo info, BitSet cells) {
		return x<0 || x>=info.width || y<0 || y>=info.height || cells.get(x+y*info.width);
    }

	public int toIndex(int x, int y) {
		return x+y*info.width;
	}

	public Iterable<Pair<Board, MoveEncoding>> neighbors() {
		List<Pair<Board, MoveEncoding>> result = new ArrayList<>();
		if (isFinished()) return result;
		result.add(Pair.of(neighbor(sourceAngle, sourceX-1, sourceY), MoveEncoding.W));
		result.add(Pair.of(neighbor(sourceAngle, sourceX+1, sourceY), MoveEncoding.E));
		
		if (rotateAngleCW(sourceAngle) != sourceAngle)
			result.add(Pair.of(neighbor(rotateAngleCW(sourceAngle), sourceX, sourceY), MoveEncoding.CW));
		if (rotateAngleCCW(sourceAngle) != sourceAngle)
			result.add(Pair.of(neighbor(rotateAngleCCW(sourceAngle), sourceX, sourceY), MoveEncoding.CCW));
		
		/*if (rotateAngleCW(sourceAngle) != sourceAngle) {
			result.add(Pair.of(neighbor(rotateAngleCW(sourceAngle), sourceX, sourceY), MoveEncoding.CW));
		} else {
		    result.add(Pair.of(new Board(sourceIndex+1, 0, getLockedInCells(), info), MoveEncoding.CW));
		}
		if (rotateAngleCCW(sourceAngle) != sourceAngle) {
			result.add(Pair.of(neighbor(rotateAngleCCW(sourceAngle), sourceX, sourceY), MoveEncoding.CCW));
		} else {
		    result.add(Pair.of(new Board(sourceIndex+1, 0, getLockedInCells(), info), MoveEncoding.CCW));
		}*/
		
		if ((sourceY & 1) == 0) {
			result.add(Pair.of(neighbor(sourceAngle, sourceX, sourceY+1), MoveEncoding.SE));
			result.add(Pair.of(neighbor(sourceAngle, sourceX-1, sourceY+1), MoveEncoding.SW));
		} else {
			result.add(Pair.of(neighbor(sourceAngle, sourceX, sourceY+1), MoveEncoding.SW));
			result.add(Pair.of(neighbor(sourceAngle, sourceX+1, sourceY+1), MoveEncoding.SE));
		}
		return result;
	}
	
	public Board makeMove(MoveEncoding me) {
		switch (me) {
		case W: return neighbor(sourceAngle, sourceX-1, sourceY);
		case E: return neighbor(sourceAngle, sourceX+1, sourceY);	
		case CW: return neighbor(rotateAngleCW(sourceAngle), sourceX, sourceY);
		case CCW: return neighbor(rotateAngleCCW(sourceAngle), sourceX, sourceY);
		case SE: return (sourceY & 1) == 0 ? neighbor(sourceAngle, sourceX, sourceY+1) : neighbor(sourceAngle, sourceX+1, sourceY+1);
		case SW: return (sourceY & 1) == 0 ? neighbor(sourceAngle, sourceX-1, sourceY+1) : neighbor(sourceAngle, sourceX, sourceY+1);
		default: return this;
		}
	}
	
	public Optional<Board> makeMoveIfPossible(MoveEncoding me) {
		if (getTemplate() == null)
	        return Optional.empty();
	    if (MoveEncoding.CW == me && rotateAngleCW(sourceAngle) == sourceAngle)
	        return Optional.empty();
	    if (MoveEncoding.CCW == me && rotateAngleCCW(sourceAngle) == sourceAngle)
            return Optional.empty();
	    return Optional.of(makeMove(me));
	}

	private Board neighbor(int angle, int x, int y) {
		UnitTemplate t = getTemplate();
		if (t.canMoveTo(this, angle, x, y)) {
			return new Board(sourceIndex, angle, cells, x, y, info);
		} else {
			return new Board(sourceIndex+1, 0, getLockedInCells(), info);
		}
	}
	
	public Pair<Board, Integer> clearLines() {
		BitSet newCells = (BitSet) cells.clone();
		int squashed = Bitsets.squash(newCells, info.width, info.height);
		return squashed>0?
				Pair.of(new Board(sourceIndex, sourceAngle, newCells, sourceX, sourceY, info), squashed):
				Pair.of(this, 0);
	}

	public int getWidth() {
		return info.width;
	}

	public int getHeight() {
		return info.height;
	}

	public UnitTemplate getTemplate() {
		if (sourceIndex >= info.sources.size()) return null;
		return info.sources.get(sourceIndex);
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}
	
	public boolean isFinished() {
		return getTemplate() == null;
	}
	
	public BitSet getCells() {
		return cells;
	}
	
	public int getSourceX() {
		return sourceX;
	}
	
	public int getSourceY() {
		return sourceY;
	}
	
	public int getTrueSourceX() {
		UnitTemplate t = getTemplate();
		return t == null ? sourceX : t.getOffsetX() + sourceX;
	}
	
	public int getTrueSourceY() {
		UnitTemplate t = getTemplate();
		return t == null ? sourceY : t.getOffsetY() + sourceY;
	}
	
	public int getTrueSourcePosition() {
        return toIndex(getTrueSourceX(), getTrueSourceY());
    }

    public int getSourceAngle() {
        return sourceAngle;
    }

    private int rotateAngleCCW(int a) {
		return getTemplate().prevAngle(a);
	}

	private int rotateAngleCW(int a) {
		return getTemplate().nextAngle(a);
	}
	
	@Override
	public String toString() {
		if (isFinished()) {
			return "F\n"+Bitsets.toString(cells, info.width, info.height);
		} else {
			return Bitsets.toString(getLockedInCells(), info.width, info.height);
		}
	}

	public int getLastRowFill() {
		int result = 0;
		for (int i = 0; i < info.width; i++) {
			if (isSet(i, info.height-1)) result++;
		}
		return result;
	}
	
	public int getFillPenalty() {
		int maxSize = getMaxRemainingSourceSize();
		int nSet = 0;
		int minY = -1;
		for (int y = 0; y < maxSize; y++) {
		    int nSetRow = getRow(y).cardinality();
		    nSet += nSetRow;
		    if (nSetRow > 0 && minY == -1) minY = y;
		}
		return nSet > 0 ? maxSize * (maxSize - minY): 0; 
	}
	
	public int[] getRowFills() {
		int[] result = new int[info.height];
		for (int y = 0; y < info.height; y++) {
			for (int x = 0; x < info.width; x++) {
				if (isSet(x, y)) {
					result[y]++;
				}
			}
		}
		return result;
	}
	
	public int getMaxRemainingSourceSize() {
		int maxSize = 0;
		for (int i = sourceIndex; i < info.sources.size(); i++) {
			UnitTemplate s = info.sources.get(i);
			if (s.getSize() > maxSize) maxSize = s.getSize();
		}
		return maxSize;
	}
	
	public int getFillCount() {
		return cells.cardinality();
	}
	
	public int getLastRowFillOnLock() {
		UnitTemplate template = getTemplate();
		if (template == null || info.height - 1 - sourceY > template.getSize()) {
			return getLastRowFill();
		}
		BitSet newCells = getLockedInCells();
		int result = 0;
		for (int i = 0; i < info.width; i++) {
			if (isSetS(i, info.height-1, info, newCells)) result++;
		}
		return result;
	}
	
	public int getFirstNotEmptyRowFill() {
		int result = 0;
		boolean isRowFilled = false;
		for (int y = 0; y < info.height; y++) {
			for (int x = 0; x < info.width; x++) {
				if (isSet(x, y)) {
					result++;
					isRowFilled = true;
				}
			}
			if (isRowFilled) return result;
		}
		return result;
	}
	
	public int getNFilledRows() {
		for (int y = 0; y < info.height; y++) {
			for (int x = 0; x < info.width; x++) {
				if (isSet(x, y)) {
					return info.height-y;
				}
			}
		}
		return 0;
	}
	
	public BitSet getLockedInCells() {
		if (getTemplate() == null) return cells;
		BitSet newCells = (BitSet) cells.clone();
		getTemplate().updateBitSet(newCells, info.width, sourceAngle, sourceX, sourceY);
		return newCells;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cells == null) ? 0 : cells.hashCode());
		result = prime * result + sourceAngle;
		result = prime * result + sourceIndex;
		result = prime * result + sourceX;
		result = prime * result + sourceY;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Board other = (Board) obj;
		if (sourceAngle != other.sourceAngle)
			return false;
		if (sourceIndex != other.sourceIndex)
			return false;
		if (sourceX != other.sourceX)
			return false;
		if (sourceY != other.sourceY)
			return false;
		if (!cells.equals(other.cells))
			return false;
		return true;
	}
	
	public IntArrayList getPossibleMovesFrom(int index) {
		IntArrayList result = new IntArrayList();
		int x = index % info.width;
		int y = index / info.width;
		
		addPossibleMove(result, x-1, y); 
		addPossibleMove(result, x+1, y);
		if ((y & 1) == 0) {
			addPossibleMove(result, x, y+1);
			addPossibleMove(result, x-1, y+1);
			addPossibleMove(result, x-1, y-1);
			addPossibleMove(result, x, y-1);
		} else {
			addPossibleMove(result, x, y+1);
			addPossibleMove(result, x+1, y+1);
			addPossibleMove(result, x, y-1);
			addPossibleMove(result, x+1, y-1);
		}
		return result;
	}

	private void addPossibleMove(IntArrayList result, int x, int y) {
		if (!isSet(x, y)) result.add(toIndex(x, y));
	}

	public boolean isSet(int index) {
		int x = index % info.width;
		int y = index / info.width;
		return isSet(x, y);
	}

	public String getCurrentAngle() {
		if (getTemplate() == null) return "";
		return sourceAngle + " from "+getTemplate().getAllowedAngles();
	}
	
	public int getTemplateSize() {
		UnitTemplate template = getTemplate();
		return template == null ? 0 : template.getSize();
	}

	public int getSourcesRemaining() {
		return info.sources.size() - sourceIndex - 1;
	}
	
	public int getRemainingSourcesSize() {
		int size = 0;
		for (int i = sourceIndex; i < info.sources.size(); i++) {
			size += info.sources.get(i).getSize();
		}
		return size;
	}

	public ImmutableList<UnitTemplate> getRemainingSources() {
		return info.sources.subList(sourceIndex, info.sources.size());
	}
	
	/** pack intra-level info into single long*/
	private static final long YMASK = 0xFFFFL;
	private static final long XMASK = 0xFFFF0000L;
	private static final long AMASK = 0xFF00000000L;
	
	public long pack() {
		return (sourceAngle << 32) | ((sourceX & YMASK) << 16) | (sourceY & YMASK); 
	}
	
	public Board unpack(long packed) {
		short y = (short) (packed & YMASK);
		short x = (short) ((packed & XMASK) >>> 16);
		byte a = (byte) ((packed & AMASK) >>> 32);
		return new Board(sourceIndex, a, cells, x, y, info);
	}

	public int getFilledNeighbourCount(int x, int y) {
		int count = 0; 
		count += toInt(isSet(x-1, y)); 
		count += toInt(isSet(x+1, y)); 
		if ((y & 1) == 0) {
			count += toInt(isSet(x, y+1));
			count += toInt(isSet(x-1, y+1));
			count += toInt(isSet(x-1, y-1));
			count += toInt(isSet(x, y-1));
		} else {
			count += toInt(isSet(x, y+1));
			count += toInt(isSet(x+1, y+1));
			count += toInt(isSet(x, y-1));
			count += toInt(isSet(x+1, y-1));
		}
		return count;
	}
	
	private int toInt(boolean b) {
		return b ? 1 : 0;
	}

	public StaticBoardInfo getInfo() {
		return info;
	}
}