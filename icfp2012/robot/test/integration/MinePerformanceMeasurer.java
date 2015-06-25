package integration;

import model.Mine;
import model.MineFactory;
import model.Move;
import model.Path;

public class MinePerformanceMeasurer {
	private static final int WARMUP_CYCLES = 500;
	private static final int TEST_CYCLES = 1000;

	private void timeNgMine() {
		Mine mine = MineFactory.getMineFromResource("/contest10.map.txt");
		Path path = Path.fromString("ULLLULRRRRUUULLLLLLUUUUULLUULLLURUDDDDULLDLDDRRULLLDLLDDDDLLLLRDLLLURUULLLLLUURRRRUUUUULUUUULUUURDRRRLLDLDDLLLDRRUULUUUURRRRRRRRRRRRRRRRRRRRDLDRRRRLLUURRRRLLLLLLLLLDDDDRRUDLLLDDRDDRRDDDRRRRUURRDRLULLDDLDLLDDDDLDLLLLLLDRRDRDRUURURRURRDLDDRRRRRDDD");
		for (int i = 0; i < WARMUP_CYCLES; i++) {
			doNgCycle(mine, path);
		}
		long totalTime = 0;
		for (int i = 0; i < TEST_CYCLES; i++) {
			long start = System.currentTimeMillis();
			doNgCycle(mine, path);
			totalTime += System.currentTimeMillis() - start;
		}
		double averagePerGame = (double) totalTime / TEST_CYCLES;
		double averagePerMove = averagePerGame / path.getMoves().length; 
		System.out.println("NG mine performance:" +1000.0/averagePerMove+" moves per sec");
	}
	
	private int doNgCycle(Mine mineNg, Path path) {
		int hash = 0;
		for (Move move : path.getMoves()) {
			mineNg = mineNg.makeMove(move);
			hash += mineNg.hashCode();
		}
		return hash;
	}

	public static void main(String[] args) {
		/*new MinePerformanceMeasurer().timeOldMine();
		new MinePerformanceMeasurer().timeImmutableMine();*/
		new MinePerformanceMeasurer().timeNgMine();
	}
}
