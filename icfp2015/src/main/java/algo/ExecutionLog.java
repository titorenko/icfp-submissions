package algo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.Board;
import verifier.SolutionVerifier;

public class ExecutionLog {
	
	public static int nProblem;
	public static int seed;
	private int loglevel;
	private BufferedWriter logFile;
	private Board initial;
	private SearchNode bestSolution;
	
	public static final ExecutionLog INSTANCE = new ExecutionLog(0);

	
	//0 - no log
	//2 - everything
	private ExecutionLog(int loglevel) {
		try {
			this.loglevel = loglevel;
			this.logFile = new BufferedWriter(new FileWriter(new File("exec.log")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		 
	}
	
	public void onNextNode(SearchNode node) {
		if (loglevel < 2) return;
		printNode(node);
	}

	private void printNode(SearchNode node) {
		try {
			log("--=== Examining node "+node.getMoves()+" used sources "+node.getBoard().getSourceIndex()+ " ===---");
			log(node.getBoard().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void log(String str) throws IOException {
		logFile.write(str);
		logFile.write("\n");
		System.out.println(str);
	}

	public void onBestNode(int bestScore, SearchNode bestSolution, int g) {
		assert new SolutionVerifier().verifyNoRepetitions(bestSolution.getMoveEncoding(), initial);
		this.bestSolution = bestSolution;
		if (loglevel < 1) return;
		printNode(bestSolution);
		try {
			log("new best score: "+bestScore+" found units remaining "+bestSolution.getBoard().getSourcesRemaining()+", words used "+bestSolution.getPowerwordsSoFar());
			log("Submit with: "+bestSolution.getSubmissionCurl(nProblem, seed));
			logFile.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getLevel() {
		return loglevel;
	}

	private double average = 0;
	private int nLookAheads = 0;
	public void onLookAheadFinished(long duration) {
		if (loglevel < 1) return;
		average = (duration + nLookAheads * average) / (nLookAheads+1);
		nLookAheads++;
		if (nLookAheads % 25 == 0) System.out.println("look ahead "+nLookAheads+" took "+duration+ ", average is "+average+" millis, solution "+getSolution());
	}
	
	private String getSolution() {
		return bestSolution == null ? "" : "Score "+bestSolution.getScore()+", sources left "+bestSolution.getBoard().getSourcesRemaining();
	}

	public void setInitial(Board initial) {
		this.initial = initial;
	}
}
