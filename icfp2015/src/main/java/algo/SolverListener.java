package algo;

/**
 * Listen for solver events
 */
public interface SolverListener {
    // return true to stop search
    boolean iterationFinished(int iterations, int bestScore);
}
