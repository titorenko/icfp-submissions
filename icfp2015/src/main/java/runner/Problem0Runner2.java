package runner;

import java.util.Arrays;
import java.util.List;

import algo.ExecutionLog;
import algo.SearchNode;
import algo.AnnealingSolver;
import icfp.io.Parser;
import icfp.io.model.Problem;
import model.Board;

public class Problem0Runner2 {
	public static void main(String[] args) {
		int nProblem = 13;
		int seed = 0;
		SearchNode best = null;
		for(int i=0; i < 1; i++) {
    		String name = "/problems/problem_"+nProblem+".json";
    		Parser parser = new Parser();
    		Problem p = parser.parse(name);
    		ExecutionLog.nProblem = nProblem;
    		ExecutionLog.seed = p.sourceSeeds[seed];
    		List<Board> boards = p.createBoards();
    		Board board = boards.get(seed);
    		AnnealingSolver solver = new AnnealingSolver(board, AnnealingSolverConfig.LONGER);
    		long start = System.currentTimeMillis();
    		SearchNode result = solver.solve();
    		if (best == null || result.getScore() > best.getScore()) {
    		    best = result;
    		}
    		System.out.println("Annealing solver done with score "+result.getScore()+" best so far "+best.getScore()+" in "
    				+(System.currentTimeMillis() -start));
    		System.out.println("Best score "+best.getScore());
    		System.out.println("Powerwords used "+Arrays.stream(best.getPowerwordsSoFar()).filter(j -> j >0).count());
    		System.out.println("Solution\n"+best.getMoveEncoding());
		}
		System.out.println("Submit with\n"+best.getSubmissionCurl(nProblem, seed));
	}
}