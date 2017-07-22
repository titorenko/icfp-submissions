package runner;

import java.util.Random;

public class AnnealingSolverConfig extends SolverConfig {

	public static final AnnealingSolverConfig QUICK= new AnnealingSolverConfig(10, 0.5, 10);
	public static final AnnealingSolverConfig DEFAULT = new AnnealingSolverConfig(100, 0.9, 10);
	public static final AnnealingSolverConfig LONGER = new AnnealingSolverConfig(1000, 0.99, 10);
	
	public final double initialTemp;
	public final double cooldownCoefficient;
	public final int logFreq;

	public AnnealingSolverConfig(double initialTemp, double cooldownCoefficient, int logFreq) {
		super(null, 0, new Random(), false, -1);
		this.initialTemp = initialTemp;
		this.cooldownCoefficient = cooldownCoefficient;
		this.logFreq = logFreq;
	}
	
}