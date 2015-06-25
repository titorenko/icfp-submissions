package integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import model.Mine;
import model.MineFactory;
import algo.mst.InterruptableAlgorithm;
import algo.mst.MinimumSpanningTree;

public class MSTRunner {
	public static void main(String[] args) throws Exception {
		new MSTRunner().runAll();
		//new MSTRunner().runMaps("horock", 1, 1);
	}

	private int TIME_LIMIT_SEC = 150;
	
	private void runAll() throws IOException {
		int total = 0;
		total += runMaps("contest", 1, 10);
		total += runMaps("flood", 5);
		total += runMaps("trampoline", 3);
		total += runMaps("beard", 5);
		total += runMaps("horock", 3);
		logScore("grandTotal", total);
	}
	
	int runMaps(String prefix, int end) throws IOException {
		return runMaps(prefix, 1, end);
	}

	protected int runMaps(String prefix, int start, int end) throws IOException {
		int totalScore = 0;
		for (int i = start; i <= end; i++) {
			Mine mine = MineFactory.getMineFromResource("/"+ prefix + i + ".map.txt");
			MinimumSpanningTree tree = new MinimumSpanningTree(mine);
			Thread thread = startInterruptThread(TIME_LIMIT_SEC, tree);
			tree.buildTree();
			int score = tree.getBestScore();
			totalScore += score;
			logScore(prefix+i, score);
			thread.interrupt();
		}
		logScore(prefix+"tot", totalScore);
		return totalScore;
	}
	
	void logScore(String name, int score) throws IOException {
		System.out.println("Search finished "+name+" = "+score);
		File file = new File(name+".txt");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write( ("YVALUE="+score).getBytes() ) ;
		fos.close();
	}
	
	Thread startInterruptThread(final int timeLimit, final InterruptableAlgorithm algo) {
		final long start = System.currentTimeMillis();
		Thread thread = new Thread() {
			{
				setDaemon(true);
			}
			@Override
			public void run() {
				while ( (System.currentTimeMillis() - start) < timeLimit*1000 && !Thread.currentThread().isInterrupted()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
				}
				if (!Thread.currentThread().isInterrupted()) {
					algo.stopWorking();
				}
			}
		};
		thread.start();
		return thread;
	}
}
