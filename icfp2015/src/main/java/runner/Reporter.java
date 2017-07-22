package runner;

import algo.SearchNode;
import icfp.io.model.BoardInfo;

import java.io.Closeable;

/**
 * Report what we've found.
 */
public interface Reporter extends Closeable {
    void reportStart(BoardInfo info);
    void reportSolution(BoardInfo info, SearchNode result);
    void close();
}
