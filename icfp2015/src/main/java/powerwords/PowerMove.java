package powerwords;

import java.util.BitSet;

import algo.SearchNode;

public interface PowerMove {

	SearchNode getToNode();

	default int getPowerScore() {
		return getToNode().getPowerScore();
	}

	boolean isEquivalentTo(SearchNode to);
	
}

class SimplePowerMove implements PowerMove {

	private final SearchNode to;

	public SimplePowerMove(SearchNode to) {
		this.to = to;
	}
	
	@Override
	public SearchNode getToNode() {
		return to;
	}

	@Override
	public boolean isEquivalentTo(SearchNode origTo) {
		BitSet originalTarget = origTo.getBoard().getLockedInCells();
		BitSet thisTarget = to.prev.getBoard().getLockedInCells();
		return originalTarget.equals(thisTarget);
	}
}

