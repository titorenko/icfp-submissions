package algo.mst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Mine;

public class CandidateFinder {
	
	public List<Integer> getCandidates(Mine mine) {
		
		List<Integer> lambdaCells = mine.getLambdaCells();
		List<Integer> hoLambdaCells = mine.getHoLambdaCells();
		
		List<Integer> cells = new ArrayList<Integer>();
		cells.addAll(lambdaCells);
		cells.addAll(hoLambdaCells);//TODO: add down indexes?
				
		if (cells.size() == 0) return Collections.singletonList(mine.getLiftCell());
		
		List<WeightedSquare> weightedCells = getApproximateDistances(mine, cells);
		List<Integer> result = new ArrayList<Integer>();
		for (WeightedSquare ws : weightedCells) {
			result.add(ws.squareIndex);
		}
		return filter(mine, result);
	}
	
	private List<WeightedSquare> getApproximateDistances(Mine mine, List<Integer> cells) {
		List<WeightedSquare> weightedCells = new ArrayList<WeightedSquare>();
		for (Integer lambda : cells) {
			int lambdaX = lambda % mine.getWidth();
			int lambdaY = lambda / mine.getWidth();
			int distance = mine.getRobot().distanceTo(lambdaX, lambdaY);
			weightedCells.add(new WeightedSquare(lambda, distance));
		}
		Collections.sort(weightedCells);
		return weightedCells;
	}

	private List<Integer> filter(Mine mine, List<Integer> indexes) {
		List<Integer> filteredResult = new ArrayList<Integer>();
		int centerX = mine.getRobot().getX();
		int centerY = mine.getRobot().getY();
		boolean[] octantsConsidered = new boolean[8];
		for (Integer index : indexes) {
			int candidateX = index % mine.getWidth();
			int candidateY = index / mine.getWidth();
			int octant = getOctant(candidateX, candidateY, centerX, centerY);
			if (octantsConsidered[octant]) continue;
			octantsConsidered[octant] = true;
			filteredResult.add(index);
		}
		return filteredResult;
	}

	private static final double PI_QUARTER = Math.PI / 4.0;
	
	int getOctant(int candidateX, int candidateY, int centerX, int centerY) {
		double x = candidateX - centerX;
		double y = candidateY - centerY;
		double atan = Math.atan(Math.abs(y)/x);
		if (atan >= 0) {
			if(atan < PI_QUARTER) return y >= 0 ? 0 : 7;
			return y >= 0 ? 1 : 6;
		} else {
			if(atan < -PI_QUARTER) return y>=0 ? 2 : 5;
			return y >=0 ? 3 : 4;
		}
	}
	
	private class WeightedSquare implements Comparable<WeightedSquare>{
		private int squareIndex;
		private int weight;
		
		WeightedSquare(int squareIndex, int weight) {
			this.squareIndex = squareIndex;
			this.weight = weight;
		}


		@Override
		public int compareTo(WeightedSquare other) {
			return weight - other.weight;
		}
	}
}