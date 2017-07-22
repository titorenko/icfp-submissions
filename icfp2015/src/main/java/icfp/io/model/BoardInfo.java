package icfp.io.model;

import model.Board;

public class BoardInfo {
    public int problemId;
    public long problemSeed;
    public Board board;

    public BoardInfo(int problemId, long problemSeed, Board board) {
        this.problemId = problemId;
        this.problemSeed = problemSeed;
        this.board = board;
    }
}
