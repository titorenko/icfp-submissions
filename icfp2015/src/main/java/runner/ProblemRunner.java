package runner;

import algo.SearchNode;
import algo.Solver;
import icfp.io.Parser;
import icfp.io.model.BoardInfo;
import icfp.io.model.Problem;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ProblemRunner {

	private static class Config {
		List<String> probNames;
		long timeLimit = Integer.MAX_VALUE;
		long allowedSeed = Integer.MIN_VALUE;
		List<String> phrasesOfPower = null;
		List<Reporter> reporter = new ArrayList<>();
		String tag;
	}

	public static void main(String[] args) {
		Config cfg = parseArgs(args);

		Parser parser = new Parser();
		List<Problem> problems = cfg.probNames.stream().map(parser::parse).collect(toList());
		problems.stream().forEach(problem ->
				problem.createBoardStream()
						.filter(bi -> bi.problemSeed == cfg.allowedSeed || cfg.allowedSeed==Integer.MIN_VALUE)
						.forEach(bi -> solveBoard(bi, cfg.reporter))
		);
		cfg.reporter.forEach(Reporter::close);
	}

	private static Config parseArgs(String[] args) {
		Config cfg = new Config();
		cfg.probNames = new ArrayList<>();
		cfg.timeLimit = Integer.MAX_VALUE;
		cfg.allowedSeed = Integer.MIN_VALUE;
		cfg.phrasesOfPower = new ArrayList<>();
		for(int i=0;i<args.length;i++) {
			switch (args[i]) {
				case "-f" :
					cfg.probNames.add(args[++i]);
					break;
				case "-t" :
					cfg.timeLimit = Long.parseLong(args[++i]) * 1000;
					break;
				case "-p" :
					cfg.phrasesOfPower.add(args[++i]);
					break;
				case "-s":
					cfg.allowedSeed = Integer.parseInt(args[++i]);
					break;
				case "-g":
					cfg.tag = args[++i];
					if (cfg.reporter.size()>0) {
						throw new RuntimeException("Tag should be set before reporters!");
					}
					break;
				case "-r":
					String types[] = args[++i].split(",");
					for(String type : types) {
						switch(type.toLowerCase()) {
							case "human":
								cfg.reporter.add(new ConsoleReporter(cfg.tag));
								break;
							case "curl":
								cfg.reporter.add(new CurlReporter(cfg.tag));
								break;
							case "json":
								cfg.reporter.add(new SubmissionReporter(cfg.tag));
								break;
							default:
								throw new RuntimeException("Unknown output type. Could be human/curl/json.");
						}
					}
					break;
				default:
					throw new RuntimeException("Unknown parameter " + args[i]);
			}
		}
		if (cfg.reporter.isEmpty()) {
			cfg.reporter.add(new ConsoleReporter("test1"));
		}
		return cfg;
	}

	private static void solveBoard(BoardInfo info, List<Reporter> reporters) {
		reporters.forEach(r -> r.reportStart(info));
		SearchNode solution = new Solver(info.board, SolverConfig.DEFAULT, 0).solve();
		reporters.forEach(r -> r.reportSolution(info, solution));
	}
}
