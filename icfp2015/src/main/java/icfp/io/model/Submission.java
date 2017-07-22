package icfp.io.model;

import java.util.Date;

public class Submission {
    public int powerScore;
    public int seed;
    public String tag;
    public Date createdAt;
    public Integer score;
    public int authorId;
    public int teamId;
    public int problemId;
    public String solution;

    public int getPowerScore() {
        return powerScore;
    }

    public int getSeed() {
        return seed;
    }

    public String getTag() {
        return tag;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getScore() {
        return score == null? -1 : score;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "powerScore=" + powerScore +
                ", seed=" + seed +
                ", tag='" + tag + '\'' +
                ", createdAt=" + createdAt +
                ", score=" + score +
                ", authorId=" + authorId +
                ", teamId=" + teamId +
                ", problemId=" + problemId +
                ", solution='" + solution + '\'' +
                '}';
    }
}
