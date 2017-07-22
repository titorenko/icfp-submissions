package powerwords;

import java.util.BitSet;

import algo.SearchNode;

public class MultisourcePowerMove implements PowerMove {

	private SearchNode finalPosition;
	private SearchNode placedPosition;

	public MultisourcePowerMove(SearchNode placedPosition, SearchNode finalPosition) {
		this.placedPosition = placedPosition;
		this.finalPosition = finalPosition;
	}

	@Override
	public SearchNode getToNode() {
		return finalPosition;
	}

	@Override
	public boolean isEquivalentTo(SearchNode origTo) {
		BitSet originalTarget = origTo.getBoard().getLockedInCells();
		BitSet thisTarget = placedPosition.prev.getBoard().getLockedInCells();
		return originalTarget.equals(thisTarget);
	}
	
	@Override
	public int getPowerScore() {
		return PowerMove.super.getPowerScore()-200;////TODO: finalPosition.getMoveCountAfterLock()*2;//only use that when new word can be used
	}
}