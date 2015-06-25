package algo.mst;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import model.Mine;
import model.MineFactory;
import model.Path;
import model.RobotState;
import algo.mst.tree.Tree;
import algo.mst.tree.TreeNode;
import algo.mst.tree.TreeVisitor;


/**
 * TODOs:
 * map 8 find way to solve (obsTruction resolver?)
 * check map 3 and beard 2 - closed robot ? + trampoline
 * better gScore for selective next tree node when backtracking
 *  
 * 
 *
 */
public class MinimumSpanningTree implements InterruptableAlgorithm {

	private Tree tree;
	private AtomicBoolean isInterrupted = new AtomicBoolean(false);
	
	public MinimumSpanningTree(Mine initial) {
		tree = new Tree(initial);
	}
	
	public void buildTree() {
		isInterrupted.set(false);
		TreeNode currentNode = tree.getRoot();
		while(currentNode.getMine().getState() != RobotState.WINNING) {
			TreeNode nextNode = currentNode.nextChild();
			if (nextNode == null) {
				currentNode = getNextTreeItem();
			} else {
				currentNode = nextNode;
			}
			if (currentNode == null || isInterrupted.get()) break;
			log(currentNode);
		}
		printBestPath();
	}

	private void log(TreeNode currentNode) {
		System.out.println(""+currentNode.getMine().getScore()+" "+currentNode.getMine().getRobot().getPath());
	}
	
	private TreeNode getNextTreeItem() {
		final AtomicInteger bestScore = new AtomicInteger(Integer.MIN_VALUE);
		final AtomicReference<TreeNode> bestCandidate= new AtomicReference<TreeNode>();
		tree.visit(new TreeVisitor() {
			@Override
			public void visit(TreeNode node) {
				if (node.getScore()> bestScore.get() && node.hasMoreChildren()) {
					bestScore.set(node.getScore());
					bestCandidate.set(node);
				}
			}
		});
		return bestCandidate.get();
	}
	
	int bestScore = 0;
	Path bestPath = new Path();
	private void printBestPath() {
		bestScore = 0;
		bestPath = new Path();
		tree.visit(new TreeVisitor() {
			@Override
			public void visit(TreeNode node) {
				if (node.getMine().getScore() > bestScore) {
					bestScore = node.getMine().getScore();
					bestPath = node.getMine().getRobot().getPath();
				}
			}
		});
		System.out.println(bestScore+" p: "+bestPath);
	}
	
	public int getBestScore() {
		return bestScore;
	}
	
	public Path getBestPath() {
		return bestPath;
	}
	
	
	public void stopWorking() {
		isInterrupted.set(true);
	}
	
	
	public static void main(String[] args) {
		Mine mine = MineFactory.getMineFromResource("/full10.map.txt", "RRRRRUR");
		MinimumSpanningTree tree = new MinimumSpanningTree(mine);
		tree.buildTree();
	}
	
	
}