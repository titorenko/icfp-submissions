package algo.simple;

import model.Cell;
import model.Mine;

/**
 * Remove earth which is unused.
 */
public class MapSimplifier {

	private final int height;
	private final int width;
	private final Mine mine;

	MapSimplifier(Mine mine) {
		this.mine = mine;
		this.height = mine.getHeight();
		this.width = mine.getWidth();
	}

	public Mine wipeEarth() {
		if (mine.getBeardState().getBeardCount() > 0) return mine;
		Mine newMine = mine.clone();
		for (int y = height - 1; y >= 0; y--) {
			if (canBeCleared(y, newMine)) {
				for(int x=0; x< width; x++) {
					if (newMine.get(x, y) == Cell.EARTH) newMine.set(x, y, Cell.EMPTY);
				}
			} else {
				break;
			}
		}
		return newMine;
	}

	boolean canBeCleared(int ey, Mine newMine) {
		for (int y=ey; y<height;y++) {
			int baseIndex = y*width;
			for (int x = 0; x < width; x++) {
				Cell cell = newMine.unsafeGet(baseIndex+x);
				if (cell.isHardFallingThing()) {
					return false;
				}
			}
		}
		return true;
	}

}
