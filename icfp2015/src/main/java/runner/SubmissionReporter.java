package runner;

import algo.SearchNode;
import icfp.io.model.BoardInfo;

import java.io.IOException;

/**
 *
 */
public class SubmissionReporter implements Reporter {
    private final String tag;

    public SubmissionReporter(String tag) {
        this.tag = tag;
    }

    @Override
    public void reportStart(BoardInfo info) {
    }

    @Override
    public void reportSolution(BoardInfo info, SearchNode result) {
        System.out.println(result.getSubmissionJson(info.problemId, info.problemSeed, tag));
    }

    @Override
    public void close() {
    }
}
