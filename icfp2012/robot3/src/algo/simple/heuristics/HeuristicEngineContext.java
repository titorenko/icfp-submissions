package algo.simple.heuristics;

import java.util.HashSet;
import java.util.Set;

public class HeuristicEngineContext {

	private Set<Integer> closedSquares = new HashSet<Integer>();
	private boolean isInitialEvaluation = true;
	
	public void setFollowUpEvalMode() {
		isInitialEvaluation = false;
	}
	
	public boolean isInitialEvalutation() {
		return isInitialEvaluation;
	}
	
	public void foundClosedSquare(Integer lambdaIndex) {
		closedSquares.add(lambdaIndex);
	}

	public boolean isClosedSquare(Integer idx) {
		return closedSquares.contains(idx);
	}


}
