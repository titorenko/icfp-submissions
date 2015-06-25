package algo.astar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


import model.Mine;
import model.Move;
import model.Path;

public class AStar {
	private final int goalSquareIndex;
	private final int goalX;
	private final int goalY;
	
	private final OpenSet openSet;
	private final Set<Integer> closedSet;


	public AStar(Mine mine, int goalSquareIndex) {
		this.goalSquareIndex = goalSquareIndex;
		
		this.goalX = goalSquareIndex % mine.getWidth();
		this.goalY = goalSquareIndex / mine.getWidth();
		
		this.openSet = new OpenSet();
		ScoredSquare start = new ScoredSquare(mine, 0, mine.getRobot().distanceTo(goalX, goalY));
		openSet.add(start);
		
		this.closedSet = new HashSet<Integer>();
	}
	
	public Path findPathToSquare() {
		
		
		while(!openSet.isEmpty()) {
			ScoredSquare current = openSet.head();
			if (current.getIndex() == goalSquareIndex)  {
				return current.getMine().getRobot().getPath();
			}
			boolean added = closedSet.add(current.getMine().hashCode());
			if (!added) continue;
			//System.out.println("adding to closed "+current.getMine().hashCode()+" os "+openSet);
			List<Move> possibleMoves = current.getMine().getPossibleRobotMoves();
			for (Move move : possibleMoves) {
				Mine newMine = current.getMine().makeMove(move);
				if (newMine.getState().isDead()) continue;
				if (closedSet.contains(newMine.hashCode())) continue;
				ScoredSquare neighbor = new ScoredSquare(newMine, current.gScore + 1, newMine.getRobot().distanceTo(goalX, goalY));
				ScoredSquare visited = openSet.get(newMine);
				//System.out.println(newMine.getRobot().getPath()+" "+neighbor.getFScore());
				if (visited == null || visited.gScore > neighbor.gScore) {
					openSet.replace(visited, neighbor);
				}
			}
		}
		throw new RuntimeException("path not found");
	}

	
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
