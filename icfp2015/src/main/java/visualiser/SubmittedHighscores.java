package visualiser;

import java.util.List;

import icfp.io.Parser;
import icfp.io.model.Submission;

public class SubmittedHighscores {
	public static void main(String[] args) {
        List<Submission> subms = new Parser().parseSubmission("/submissions.json");
        Submission[] best = new Submission[25]; 
        for (Submission submission : subms) {
        	if (submission.seed != 0) continue;
        	int id = submission.problemId;
        	if (best[id] == null || (best[id].getScore() < submission.getScore())) {
        		best[id] = submission;
        	}
		}
        for (int i = 0; i < best.length; i++) {
			System.out.println(i+": "+best[i].score+",  seed "+best[i].seed+", powerscore "+best[i].powerScore);
		}
	}
}
