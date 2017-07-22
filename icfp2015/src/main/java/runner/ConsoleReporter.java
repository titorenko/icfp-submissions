package runner;

import algo.SearchNode;
import icfp.io.model.BoardInfo;

public class ConsoleReporter implements Reporter {

    private final String tag;

    public ConsoleReporter(String tag) {
        this.tag = tag;
    }

    @Override
    public void reportStart(BoardInfo info) {
        System.out.println(String.format("Solving problem %d/%d", info.problemId, info.problemSeed));
    }

    @Override
    public void reportSolution(BoardInfo info, SearchNode result) {
        System.out.println("Found result with score : " + result.getScore());
        System.out.println(result.getSubmissionCurl(info.problemId, info.problemSeed, tag));
    }

    @Override
    public void close() {
    }
}
