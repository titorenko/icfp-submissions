package runner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import algo.ExecutionLog;
import algo.GreedySolver;
import algo.SearchNode;
import icfp.io.Parser;
import icfp.io.model.Problem;
import model.Board;
import powerwords.Powerwords;

public class GreadyProblem0Runner {

	public static void main(String[] args) throws IOException {
		SolverConfig cfg = new SolverConfig(Arrays.asList(7, 5, 5, 2, 1), 0, new Random(0), false, 15);
		
		int nProblem = 2;
		int maxRuns = 5;
		int maxSeed = 1;
		for (int seed = 0; seed < maxSeed; seed++) {
			SearchNode best = null;
			for(int i=0; i < maxRuns; i++) {
	    		String name = "/problems/problem_"+nProblem+".json";
	    		Parser parser = new Parser();
	    		Problem p = parser.parse(name);
	    		ExecutionLog.nProblem = nProblem;
	    		ExecutionLog.seed = p.sourceSeeds[seed];
	    		List<Board> boards = p.createBoards();
	    		Board board = boards.get(seed);
	    		GreedySolver solver = new GreedySolver(board, cfg);
	    		SearchNode result = solver.solve();
	    		if (best == null || result.getScore() > best.getScore()) {
	    		    best = result;
	    		}
	    		System.out.println("Run "+i+" for problem "+nProblem+"#"+seed+", solved with score "+result.getScore()+" best so far "+best.getScore());
	    		System.out.println("Best score "+best.getScore());
	    		System.out.println("Unused powerwords: "+getUnused(best));
	    		System.out.println("Powerwords used "+Arrays.stream(best.getPowerwordsSoFar()).filter(j -> j >0).count());
	    		System.out.println("Solution\n"+best.getMoveEncoding());
			}
			String result = "Submit with\n"+best.getSubmissionCurl(nProblem, ExecutionLog.seed);
			result+="\nFound with schedule: "+cfg.schedule+" and multiplier "+cfg.fillFactor +" over "+maxRuns;
			System.out.println(result);
			//FileUtils.write(new File("problem"+nProblem+"_y_"+seed+".result"), "Best score "+best.getScore()+"\n"+result);
		}
	}

	private static String getUnused(SearchNode best) {
		int[] powerwordsSoFar = best.getPowerwordsSoFar();
		Set<String> s = new HashSet<String>(Powerwords.load().toJavaList().stream().map(p -> p.toLowerCase()).collect(Collectors.toList()));
		int idx = 0;
		for (int f : powerwordsSoFar) {
			if (f > 0) s.remove(Powerwords.INSTANCE.ord(idx).toString().toLowerCase());
			idx++;
		}
		return s.toString();
	}
}