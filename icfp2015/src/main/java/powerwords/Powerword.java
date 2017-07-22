package powerwords;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import algo.SearchNode;
import model.Board;
import model.MoveEncoding;

public class Powerword implements Comparable<Powerword> {
    public final int ord;
    private final String representation;
    private final List<MoveEncoding> moves = new ArrayList<>();

    public Powerword(String representation, int ord) {
    	this.ord = ord;
        this.representation = representation;
        for(char c: representation.toLowerCase().toCharArray()){
            moves.add(MoveEncoding.encodingOf(c));
        }
    }

    public Object applyTo(SearchNode from) {
        SearchNode cur = from;
        int idx = 0;
        for (MoveEncoding me : moves) {
            Optional<Board> nextBoard = cur.getBoard().makeMoveIfPossible(me);
            if (!nextBoard.isPresent()) return null;
            cur = cur.next(Pair.of(nextBoard.get(), me));
            if (cur.isLastMovePlacement()) {
            	return getPrefixInfo(cur, idx);
            }
            idx++;
        }
        cur.onPowerWord(this);
        return cur;
    }
    
    private Object getPrefixInfo(SearchNode cur, int idx) {
    	  PrefixInfo pi = new PrefixInfo(cur, this, moves.subList(idx+1, moves.size()));
    	  return pi.finalPosition == null ? null : pi;
	}

	@Override
    public String toString() {
        return representation;
    }

    @Override
    public int compareTo(Powerword o) {
        return representation.compareTo(o.representation);
    }

    public int size() {
        return representation.length();
    }
    
    public static class PrefixInfo {
        public final SearchNode placedPosition;
        public final Powerword powerWord;
        public final SearchNode finalPosition;
        
        public PrefixInfo(SearchNode placedPosition, Powerword powerWord,  List<MoveEncoding> movesRemaining) {
            this.placedPosition = placedPosition;
            this.powerWord = powerWord;
            this.finalPosition = getFinalPosition(movesRemaining);
        }

        public SearchNode getFinalPosition(List<MoveEncoding> movesRemaining) {
            SearchNode cur = placedPosition;
            for (MoveEncoding me : movesRemaining) {
                Optional<Board> nextBoard = cur.getBoard().makeMoveIfPossible(me);
                if (!nextBoard.isPresent()) return null;
                cur = cur.next(Pair.of(nextBoard.get(), me));
                if (cur.isLastMovePlacement()) {
                    return null;
                }
            }
            cur.onPowerWord(powerWord);
            return cur;
        }
    }

}