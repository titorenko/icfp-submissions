package algo.astaralgo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import model.Mine;
import model.Move;
import model.Path;
import model.RobotState;
import algo.simple.heuristics.Delta;
import algo.simple.heuristics.HeuristicalEngine;

public class AStarAlgo {
	private final OpenSet openSet;
	private final Set<Integer> closedSet;
	private HeuristicalEngine engine;


	public AStarAlgo(Mine mine, HeuristicalEngine engine) {
		this.engine = engine;
		this.openSet = new OpenSet();
		ScoredSquare start = new ScoredSquare(mine, 0, 0);
		openSet.add(start);
		
		this.closedSet = new HashSet<Integer>();
	}
	
	public Path findPath() {
		while(!openSet.isEmpty()) {
			ScoredSquare current = openSet.head();
			if (current.getMine().getState() == RobotState.WINNING)  {
				return current.getMine().getRobot().getPath();
			}
			boolean added = closedSet.add(current.getMine().hashCode());
			if (!added) continue;
			List<Move> possibleMoves = current.getMine().getPossibleRobotMoves();
			for (Move move : possibleMoves) {
				Mine newMine = current.getMine().makeMove(move);
				if (newMine.getState().isDead()) continue;
				if (closedSet.contains(newMine.hashCode())) continue;
				Delta delta = engine.applyHeuristics(newMine);
				/*if (delta.isDeadEnd()) {
					System.out.println(newMine);
				}*/
				//if (newMine.getState() != RobotState.WINNING && delta.isDeadEnd()) continue;
				record(current.getMine());
				ScoredSquare neighbor = new ScoredSquare(newMine, current.getMine().getScore(), delta.getValue());
				/*System.out.println(newMine.getRobot().getPath()+" "+neighbor.getFScore());
				System.out.println(newMine);*/
				ScoredSquare visited = openSet.get(newMine);
				if (visited == null || visited.gScore < neighbor.gScore) {
					openSet.replace(visited, neighbor);
				}
			}
		}
		System.out.println("path not found");
		return bestPath;
	}

	int maxScore = 0;
	Path bestPath= new Path();;
	private void record(Mine mine) {
		int score = mine.getScore();
		if (score > maxScore) {
			maxScore = score;
			bestPath = mine.getRobot().getPath();
			System.out.println(maxScore+" "+bestPath);
		}
	}
	
	
	/*public static void main(String[] args) {
		Mine mine = MineFactory.getMineFromResource("/contest3.map.txt", "LDDDRRRRDDL");
		HeuristicalEngine engine = EvaluatorFactory.getEngine(mine);
		System.out.println(engine.applyHeuristics(mine));
	}*/
	
}


class OpenSet {
	private final Map<Mine, ScoredSquare> scoreMap = new HashMap<Mine, ScoredSquare>();
	private final PriorityQueue<ScoredSquare> queue = new PriorityQueue<ScoredSquare>();
	
	public void add(ScoredSquare square) {
		scoreMap.put(square.getMine(), square);
		queue.add(square);
	}

	public void replace(ScoredSquare olds, ScoredSquare news) {
		if (olds != null) queue.remove(olds);
		add(news);
	}

	public ScoredSquare get(Mine mine) {
		return scoreMap.get(mine);
	}

	public ScoredSquare head() {
		ScoredSquare result = queue.poll();
		scoreMap.remove(result.getMine());
		return result;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	
}
