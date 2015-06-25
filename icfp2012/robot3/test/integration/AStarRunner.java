/*package integration;

import java.io.IOException;

import model.Mine;
import model.MineFactory;
import model.Path;
import algo.astaralgo.AStarAlgo;
import algo.simple.Evaluator;
import algo.simple.EvaluatorFactory;
import algo.simple.heuristics.HeuristicalEngine;

public class AStarRunner extends EvalutatorRunner {
	
	protected int runMaps(String prefix, int start, int end) throws IOException {
		int totalScore = 0;
		for (int i = start; i <= end; i++) {
			Mine mine = MineFactory.getMineFromResource("/"+ prefix + i + ".map.txt");
			HeuristicalEngine engine = EvaluatorFactory.getEngine(mine);
			AStarAlgo aStarAlgo = new AStarAlgo(mine, engine);
			Thread thread = startInterruptThread(Evaluator.TIME_LIMIT_SEC);
			Path path = aStarAlgo.findPath();
			int score = mine.makeMoves(path).getScore();
			totalScore += score;
			logScore(prefix+i, score);
			thread.interrupt();
		}
		logScore(prefix+"tot", totalScore);
		return totalScore;
	}
	

	public static void main(String[] args) throws IOException {
		AStarRunner runner = new AStarRunner();
		runner.startConsoleListener();
		//runner.doFullRun();
		runner.runMaps("full", 10, 10);
	}
}
*/