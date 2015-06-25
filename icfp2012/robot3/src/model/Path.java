package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class Path implements Iterable<Move>{
	List<Move> moves = new ArrayList<Move>();
	
	public Path(Move[] moves) {
		this.moves.addAll(Arrays.asList(moves));
	}
	
	public Path() {
		
	}

	public Path(List<Move> moves) {
		this.moves = new ArrayList<Move>(moves);
	}

	public Path addMove(Move move) {
		Path newPath = new Path(moves);
		newPath.moves.add(move);
		return newPath;
	}
	
	public Path insertMove(Move move, int index) {
		Path newPath = new Path(moves);
		newPath.moves.add(index, move);
		return newPath;
	}
	
	public Path subpath(int fromIndex) {
		return subpath(fromIndex, length());
	}
	
	public Path subpath(int fromIndex, int toIndex) {
		Path newPath = new Path(moves);
		newPath.moves = newPath.moves.subList(fromIndex, toIndex);
		return newPath;
	}

	public boolean isPrefixOf(Path path) {
		if (length() >= path.length()) return false;
		for (int i = 0; i < moves.size(); i++) {
			if (moves.get(i) != path.moves.get(i)) return false;
		}
		return true;
	}


	public Move[] getMoves() {
		return moves.toArray(new Move[0]);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Move move : moves) {
			builder.append(move.toString());
		}
		return builder.toString();
	}

	public int length() {
		return moves.size();
	}

	public Path removeMove(int index) {
		Path newPath = new Path(moves);
		newPath.moves.remove(index);
		return newPath;
	}
	
	public Path removeLastMove() {
		int length = length();
		if (length == 0) return this;
		Path newPath = new Path(moves);
		newPath.moves.remove(length - 1);
		return newPath;
	}

	public static Path fromString(String moveString) {
		Collection<Move> moves = new ArrayList<Move>(moveString.length());
        for(int i=0;i<moveString.length();i++) {
            moves.add(Move.valueOf(moveString.substring(i, i + 1)));
        }
        return new Path(moves.toArray(new Move[moves.size()]));
	}

	@Override
	public Iterator<Move> iterator() {
		return moves.iterator();
	}

	@Override
	public int hashCode() {
		return moves.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Path other = (Path) obj;
		return moves.equals(other.moves);
	}

	

}