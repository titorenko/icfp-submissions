package runner;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import algo.SearchNode;
import algo.Solver;
import icfp.io.Parser;
import icfp.io.model.BoardInfo;
import icfp.io.model.Problem;

public class QuickCheckerAll {
	public static void main(String[] args) {
		Parser parser = new Parser();
		List<String> probNames = new ArrayList<>();
		for (int pi = 0; pi < 24; pi++) {
			probNames.add("/problems/problem_"+pi+".json");
		}
		long start = System.currentTimeMillis();
		List<Problem> problems = probNames.stream().map(parser::parse).collect(toList());
		int sum = problems.stream().mapToInt(problem ->
			problem.createBoardStream().limit(1).mapToInt(bi -> solveBoard(bi)).sum()
		).sum();
		System.out.println("Total sum: "+sum+", verified in "+(System.currentTimeMillis() - start));
	}

	private static int solveBoard(BoardInfo info) {
		System.out.println("Solving "+info.problemId);
		SearchNode solution = new Solver(info.board, SolverConfig.QUICK, 0).solve();
		return solution.getScore();
	}
}
