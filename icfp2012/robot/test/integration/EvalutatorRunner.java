package integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import model.Mine;
import model.MineFactory;
import model.Path;
import algo.simple.Evaluator;
import algo.simple.EvaluatorFactory;

public class EvalutatorRunner {
	// 1, best - 212, our - 212                 
	// 2, best - 281, our - 281 				
	// 3, best - 275, our - 275					LDDDRRRRDDLLLLLDURRRUURRR										 
	// 4, best - 575, our -  561					 
	// 5, best - 1303, our - 1299					 
	// 6, best - 1177, our - 1139
	// 7, best - 869, our -  867
	// 8, best - 1973, our -  1965				UUUUUUDDDDLRRLDDRRRRRURWLURURWLUURUUUULLULLLLLLUUULLLUULLLLLRRDDDDDDLDRDDDLLRRUUUULLDDUUUURRUUUURRDDDDDDDDDRDRRDDDDLLLLLRRRRRDRRD
	// 9, best - 3093, our -  3073				LRRLUURRRDLDRRRRRRUUURURLDRRRLDDDRRRRRRRUURRRRDDLLLLUULLRDRRLLDLLULLLLUULUUUUULLLLLLURRUURUURRRURRRDDDRRRDDRRRRRDULULDLLLUULLLLLLLLDLLLLLDLLRURRRRURUUUU
	// 10, best - 3634, our -  3593				ULLLULRRRRUUULLLLLLUUUUULLUULLLURUDDDDULLDLDDRRULLLDLLDDDDLLLLRDLLLURUULLLLLUURRRRUUUUULUUUULUUURDRRRLLDLDDLLLDRRUULUUUURRRRRRRRRRRRRRRRRRRRDLDRRRRLLUURRRRLLLLLLLLLDDDDRRUDLLLDDRDDRRDDDRRRRUURRDRLULLDDLDLLDDDDLDLLLLLLDRRDRDRUURURRURRDLDDRRRRRDDD
	
	//	13392 -	13265 = 127 
	
	//1, best - 945,  our 943
	//2, best - 281,  our 281
	//3, best - 1303, our 1299
	//4, best - 1592, our 968
	//5, best - 575,  our 561
	
	// 4696 - 4052 = 654
	
	//1, 426   - 291
	//2, 1742  - 1734
	//3, 5477  - 3135
	//7645 - 5160 = 2485
	
	
	//1, 860 
	//2, 4523 
	//3, 1793
	//4, 3103
	//5, 966
	
	
	//ho
	// 762 - 715
	// 747 - 382
	// 2406 - 625
	
	// full
	// 1161 - 1146 - 1155
	// 1589 - 932  - 1578
	// 2832 - 632  - 2838
	// 2170 - 2094 - 944
	// 4815 - 2742 - 3004
	//------- 7546 - 9519
	
	//full2 
	// 5198 - 2652 - 2641
	// 666? - 660 - 654
	// 2601 - 1398 - 2153
	// 3671 - 1106 - 964
	// 5749 - 0 - 0
	//      - 13362 - 15931

	static Evaluator e = null;

	CountDownLatch startSignal = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException {
		EvalutatorRunner runner = new EvalutatorRunner();
		runner.startConsoleListener();
		runner.doFullRun();		
	}//TODO: at map beard2 and trampoline3 robot gets lost because of wrong lambda distance


	protected void doFullRun() throws IOException {
		int total = runMaps("contest", 10);
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
			e = EvaluatorFactory.getDefaultEvaluator(mine);
			Thread thread = startInterruptThread(Evaluator.TIME_LIMIT_SEC);
			Path path = e.getPath(mine);
			int score = mine.makeMoves(path).getScore();
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
	
	void startConsoleListener() {
		new Thread() {
			{
				setDaemon(true);
			}
			@Override
			public void run() {
				while(true) {
					try {
						int c = System.in.read();
						if (c == 'c') {
							e.stopWorking();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	Thread startInterruptThread(final int timeLimit) {
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
					e.stopWorking();
				}
			}
		};
		thread.start();
		return thread;
	}
}
