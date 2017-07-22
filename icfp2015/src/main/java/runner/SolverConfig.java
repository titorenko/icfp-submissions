package runner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SolverConfig {
	
	public static final SolverConfig SLOW = new SolverConfig(Arrays.asList(10, 5, 5, 5, 2, 2, 1, 1, 1, 1), 0, new Random(), true, -1);
	
	public static final SolverConfig DEFAULT = new SolverConfig(Arrays.asList(7, 5, 5, 2, 1), 0, new Random(), true, -1);
	
	public static final SolverConfig QUICK = new SolverConfig(Arrays.asList(3, 1, 1), 0, new Random(0), false, 15);

	public final List<Integer> schedule;

	public final int fillFactor;
	
	public final Random rnd;

	public final boolean doCrossLevelPowerwordSearch;

	public final int timeLimitSeconds;
	
	protected SolverConfig(List<Integer> schedule, int fillFactor, Random rnd, boolean doCrossLevelPowerwordSearch, int timeLimitSeconds) {
		this.schedule = schedule;
		this.fillFactor = fillFactor;
		this.rnd = rnd;
		this.doCrossLevelPowerwordSearch = doCrossLevelPowerwordSearch; 
		this.timeLimitSeconds = timeLimitSeconds;
	}
}

/**
   return Arrays.asList(15, 7, 5, 5, 5, 5, 2, 2, 1); - ridiculous
		/*if (board.getHeight()*board.getWidth() > 120) {
			return Arrays.asList(7, 5, 5, 2, 1);//7, 5, 5, 2, 1 4169 on 0
		} else {
			//return Arrays.asList(15, 2, 2, 1, 1);
			return Arrays.asList(15, 2, 2, 1, 1);
			
			//return Arrays.asList(7, 5, 5, 2, 1);
			//return Arrays.asList(7, 5, 5, 2, 1, 1);
			//return Arrays.asList(7, 5, 5, 2, 1, 1, 1);
			//return Arrays.asList(10, 5, 5, 5, 2, 2, 1 );
			//return Arrays.asList(3, 3, 3, 3, 3, 2, 2, 2, 1, 1);
			//return Arrays.asList(15, 7, 5, 5, 5, 5, 2, 2, 1, 1, 1, 1);
			//return Arrays.asList(10, 5, 5, 5, 2, 2, 1, 1, 1, 1);//10, 5, 5, 5, 2, 2, 1 - quite good (4469 on 0)
			//10, 5, 5, 5, 2, 2, 1, 1, 1, 1  - brd 9
*/