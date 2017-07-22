/*package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import model.Board;
import powerwords.PowerMove;
import powerwords.PowerWordExplorer;

public class SingleModeStrategy {

	private SearchNode sn;

	public SingleModeStrategy(SearchNode sn) {
		this.sn = sn;
	}

	public SearchNode selectNextMove() {
		int nFilledRows = sn.getBoard().getNFilledRows();
		int nSources = sn.getBoard().getSourcesRemaining();
		if (nSources <= nFilledRows|| nFilledRows >= sn.getBoard().getHeight()-1) {
			return null;//fallback to general strategy;  
		}
		int y = getTargetY();
		if (y < 0) return null;
		int yFill = sn.getBoard().getRow(y).cardinality();
		if (yFill < sn.getBoard().getWidth() / 2 && y < sn.getBoard().getHeight() - 1) return null;
		return fillLowestRowWithoutClearance(y);
	}

	private SearchNode fillLowestRowWithoutClearance(int y) {
		List<Pair<Integer, Integer>> xs = getTargetX(y);
		for (Pair<Integer, Integer> x : xs) {
			SearchNode path = findPathTo(x.getLeft(), y);
			if (path != null) return path;
		}
		return null;
	}

	private SearchNode findPathTo(int x, int y) {
		Board sb = sn.getBoard();
		int sourceX = x - sb.getTemplate().getOffsetX();
		int sourceY = y - sb.getTemplate().getOffsetY();
		Board tb = new Board(sb.getSourceIndex(), sb.getSourceAngle(), sb.getCells(), sourceX, sourceY, sb.getInfo());
		SearchNode target = SearchNode.initial(tb, 0);
		PowerWordExplorer pwex = new PowerWordExplorer(sn, target);
	    PowerMove pw = pwex.findPowerWordPathFromTo();
		return pw == null ? null : pw.getToNode();
	}

	private List<Pair<Integer, Integer>> getTargetX(int y) {
		List<Pair<Integer, Integer>> xs = new ArrayList<>();
		Board board = sn.getBoard();
		for (int x = 0; x < board.getWidth(); x++) {
			if (board.isSet(x, y)) continue;
			int fill = board.getFilledNeighbourCount(x, y);
			xs.add(Pair.of(x, fill));
		}
		Collections.sort(xs, Collections.reverseOrder(Comparator.comparing(Pair::getRight)));
		return xs;
	}

	private int getTargetY() {
		Board board = sn.getBoard();
		for (int y = board.getHeight()-1; y>=0; y--) {
			int nFill = board.getRow(y).cardinality();
			if (nFill < sn.getBoard().getWidth() - 1) {
				return y;
			}
		}
		return -1;
	}

}*/