package model;

/**
 *
 */
public class Random {

    private static final long multiplier = 1103515245L;
    private static final long increment = 12345L;
    private static final long mask = 0xffffffffL;

    private long seed;

    public Random(long seed) {
        this.seed = seed;
    }

    public int next() {
        int value = valueOf(seed);
        seed = nextSeed(seed);
        return value;
    }

    public static long nextSeed(long seed) {
        return (seed*multiplier + increment) & mask;
    }

    public static int valueOf(long seed) {
        return (int) ((seed >> 16) & 0x7FFF);
    }
}
