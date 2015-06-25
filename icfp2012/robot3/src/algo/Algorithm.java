package algo;

import model.Mine;

/**
 * Calculate the best!
 */
public interface Algorithm {
    // do calculations
    String getPath(Mine mine);
    // stop calculating immediately and return best result found so far
    void stopWorking();
    // get mine state after execution (?)
    Mine getTerminalMine();
}
