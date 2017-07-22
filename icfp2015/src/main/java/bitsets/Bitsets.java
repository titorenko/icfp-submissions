package bitsets;

import java.util.BitSet;

public class Bitsets {
	static final char zero = '.';
	static final char one = '*';
	
	public static BitSet from(String repr) {
		BitSet result = new BitSet(repr.length());
		for (int i=0; i<repr.length();i++) {
			if (repr.charAt(i) == one) {
				result.set(i);
			}
		}
		return result;
	}

	public static String toString(BitSet cells, int width, int height) {
		StringBuffer result = new StringBuffer();
		for (int y = 0; y < height; y++) {
			if (y % 2 == 1) result.append(" ");
			for (int x = 0; x < width; x++) {
				result.append(cells.get(x+y*width) ? one : zero).append(' ');
			}
			if (y!=height-1) result.append("\n");
		}
		return new String(result);
	}

	public static boolean isFull(BitSet set, int y, int width) {
		for(int i=y*width;i<(y+1)*width;i++) {
			if (!set.get(i)) return false;
		}
		return true;
	}

	public static void copyLine(BitSet set, int yFrom, int yTo, int width) {
		int j=yTo*width;
		for(int i=yFrom*width;i<(yFrom+1)*width;i++) {
			set.set(j++, set.get(i));
		}
	}

	public static void clearLine(BitSet set, int y, int width) {
		set.clear(y*width, (y+1)*width);
	}

	public static int squash(BitSet set, int width, int height) {
		int removed = 0;
		int to = height - 1;
		for(int from=height-1;from>=0;from--) {
			if (isFull(set, from, width)) {
				to++;
				removed++;
			} else if (to!=from) {
				copyLine(set, from, to, width);
			}
			to--;
		}
		for(int i=0;i<removed;i++) {
			clearLine(set, i, width);
		}
		return removed;
	}
}
