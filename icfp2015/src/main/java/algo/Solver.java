package algo;

import model.Board;
import runner.SolverConfig;

public class Solver {
	private static final int LOG_LEVEL = 0;

	private int bestScore = Integer.MIN_VALUE;
	private SearchNode bestSolution = null;
	private final SolverConfig cfg;
	
	private MinPQ<SearchNode> queue;
	
	int nIterations;

	private long startTime;

	public Solver(Board initial, SolverConfig cfg) {
		this(initial, cfg, LOG_LEVEL);
	}

	public Solver(Board initial, SolverConfig cfg, int logLevel) {
		ExecutionLog.INSTANCE.setInitial(initial);
		this.cfg = cfg;
		this.queue = new MinPQ<>(initial.getWidth()*initial.getHeight(), new HeuristicComparator());
		this.queue.insert(SearchNode.initial(initial, cfg));
	}
	
	public SearchNode solve() {
		this.startTime = System.currentTimeMillis();
		nIterations = 0;
        do {
        	runSearchIteration(queue);
            nIterations++;
        } while (!stopCondition());
        return bestSolution;
	}

	private void runSearchIteration(MinPQ<SearchNode> q) {
		SearchNode node = q.delMin();
		if (node.getScore() > bestScore) {
			bestScore = node.getScore();
			bestSolution = node;
			ExecutionLog.INSTANCE.onBestNode(bestScore, bestSolution, 0);
		}
		ExecutionLog.INSTANCE.onNextNode(node);
        node.insertNextMoves(q);
	}

	private boolean stopCondition() {
		return queue.isEmpty() || (cfg.timeLimitSeconds > 0 && ((System.currentTimeMillis() - startTime) > cfg.timeLimitSeconds*1000));
	}
}