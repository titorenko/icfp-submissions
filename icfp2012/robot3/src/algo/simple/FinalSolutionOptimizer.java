package algo.simple;

import java.util.ArrayList;
import java.util.List;

import model.Mine;
import model.Move;
import model.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalSolutionOptimizer {
	private static final Logger logger = LoggerFactory.getLogger(FinalSolutionOptimizer.class);
	
	private int timeLimit;
	private final int maxCycleLength;
	 
	private final Mine initialGame;
	private final Path initialPath;

	private long start;
	
	public FinalSolutionOptimizer(Mine initialGame, Path initialPath) {
		this(initialGame, initialPath, 4, 9500);
	}
	
	public FinalSolutionOptimizer(Mine initialGame, Path initialPath, int timeLimit) {
		this(initialGame, initialPath, timeLimit <= 1000 ? 2 : 4, timeLimit);
	}
	
	FinalSolutionOptimizer(Mine initialGame, Path initialPath, int maxCycleLength, int timeLimit) {
		this.initialGame = initialGame;
		this.initialPath = initialPath;
		this.maxCycleLength = maxCycleLength;
		this.timeLimit = timeLimit;
	}
	
	public Path optimize() {
		this.start = System.currentTimeMillis();
		try {
			return optimize(initialPath, 0);
		} finally {
			logger.info(String.format("Optimize done in %d millis", getDurationInMillis()));
		}
	}

	private long getDurationInMillis() {
		return System.currentTimeMillis() - start;
	}
	
	private OptimizationMode getOptimizationMode() {
		long duration = getDurationInMillis() ;
		if (duration < timeLimit / 2) {
			return OptimizationMode.NORMAL;
		} else if (duration < timeLimit) {
			return OptimizationMode.FASTER;
		} else {
			return OptimizationMode.ABORT;
		}
	}
	
	
	Path optimize(Path path, int startIndex) {
		int bestScore = score(path);
		OptimizationMode mode = getOptimizationMode();
		if (mode == OptimizationMode.ABORT) return path;
		Move[] moves = path.getMoves();
		for (int i = startIndex; i < moves.length; i++) {
			List<Path> candidates = getCandidates(path, i, mode);
			for (Path candidate : candidates) {
				int score = score(candidate);
				if (score > bestScore) return optimize(candidate, Math.max(i-maxCycleLength, 0));
			}
		}
		return path;
	}
	
	List<Path> getCandidates(Path path, int i, OptimizationMode mode) {
		if (mode == OptimizationMode.FASTER) {
			getFastCandidates(path, i);
		}
		List<Path> result = new ArrayList<Path>();
		int bound = Math.min(i+maxCycleLength, path.length());
		for(int j=i; j<bound; j++) {
			path = path.removeMove(i);
			result.add(path);
			result.add(path.insertMove(Move.U, i).insertMove(Move.L, i));
			result.add(path.insertMove(Move.U, i).insertMove(Move.R, i));
			result.add(path.insertMove(Move.U, i).insertMove(Move.U, i));
			
			result.add(path.insertMove(Move.D, i).insertMove(Move.L, i));
			result.add(path.insertMove(Move.D, i).insertMove(Move.R, i));
			result.add(path.insertMove(Move.D, i).insertMove(Move.D, i));
			
			result.add(path.insertMove(Move.L, i).insertMove(Move.L, i));
			
			result.add(path.insertMove(Move.R, i).insertMove(Move.R, i));
			
		}
		return result;
	}

	private List<Path> getFastCandidates(Path path, int i) {
		List<Path> result = new ArrayList<Path>();
		path = path.removeMove(i);
		result.add(path);
		result.add(path.insertMove(Move.U, i));
		result.add(path.insertMove(Move.D, i));
		result.add(path.insertMove(Move.L, i));
		result.add(path.insertMove(Move.R, i));
		return result;		
	}

	int score(Path path) {
		Mine endState = initialGame.makeMoves(path);
		return endState.getScore();
	}
}

enum OptimizationMode {
	NORMAL, FASTER, ABORT;
}