package runner;

import icfp.io.CurlHelper;
import icfp.io.Parser;
import icfp.io.model.Submission;

import java.util.Comparator;
import java.util.List;

public class SubmissionBrowser {

    private static final int ALL = Integer.MIN_VALUE;
    private static final String ANY = null;

    private final List<Submission> subs;

    public SubmissionBrowser(List<Submission> subs) {
        this.subs = subs;
    }

    public void printProblemSubmissions(int problemId, String tag) {
        subs.stream()
                .filter(s -> s.problemId == problemId || problemId == ALL)
                .filter(s -> tag == ANY || (s.tag!=null && s.tag.contains(tag)))
                .sorted(Comparator
                        .comparing(Submission::getProblemId)
                        .thenComparing(Submission::getSeed)
                        .thenComparing(Submission::getCreatedAt))
                .map(SubmissionBrowser::scoreReport)
                .forEach(System.out::println);
    }

    public void printSubmittedProbs() {
        subs.stream().mapToInt(s -> s.problemId).distinct().sorted().forEach(System.out::println);
    }

    public static SubmissionBrowser fromFile(String name) {
        return new SubmissionBrowser(new Parser().parseSubmission(name));
    }

    public static SubmissionBrowser fromServer() {
        String json = CurlHelper.getSubmissionJson();
        return new SubmissionBrowser(new Parser().parseSubmissionText(json));
    }

    private static String scoreReport(Submission s) {
        return String.format("Problem %d, Seed %d, Score : %d, PScore %d, Tag : %s, Created : %s, Sol : '%s'",
                s.problemId, s.seed, s.score, s.powerScore, s.tag, s.createdAt, s.solution);
    }

    public static void main(String[] args) {
        SubmissionBrowser browser;
        if (args.length>0) {
            if (args[0].equals("-f")) {
                browser = fromFile("/submissions.json");
            } else {
                throw new RuntimeException("Set -f to load from submissions.json");
            }
        } else {
            browser = fromServer();
        }

        // problem or ALL, contained word or ALL
        browser.printProblemSubmissions(ALL, "word-check");
//        browser.printSubmittedProbs();
    }
}
