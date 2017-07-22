package runner;

import algo.SearchNode;
import icfp.io.model.BoardInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurlReporter implements Reporter {

    private final ExecutorService submissionService = Executors.newCachedThreadPool();
    private final String tag;

    public CurlReporter(String tag) {
        this.tag = tag;
    }

    @Override
    public void reportStart(BoardInfo info) {
    }

    @Override
    public void reportSolution(BoardInfo info, SearchNode result) {
        String json = result.getSubmissionJson(info.problemId, info.problemSeed, tag);
        submissionService.submit(() -> {
            try {
                System.out.println(
                        String.format("Trying to submit %d/%d with score %d and moves : '%s'",
                                info.problemId, info.problemSeed, result.getScore(), result.getMoves()));
                ProcessBuilder pb = prepareSubmitProcess(json);
                Process p = pb.start();
                String response = readStreamFully(p.getInputStream());
                writeOperationSuccessReport(p, response);
            } catch (Exception e) {
                System.err.println("Failed to submit result: " + e.getMessage());
            }
        });
    }

    @Override
    public void close() {
        submissionService.shutdown();
    }

    private static ProcessBuilder prepareSubmitProcess(String json) {
   		ProcessBuilder pb = new ProcessBuilder(
                   "curl",
                   "--user", ":w0agR+pfoRAqLcowQmQSmM8VbLuQGGvb4cdqqpv6A2Y=",
                   "-X", "POST",
                   "-ssl",
                   "-H", "Content-Type: application/json",
                   "-d", json,
                   "https://davar.icfpcontest.org/teams/238/solutions"
           );
   		pb.inheritIO();
   		pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
   		return pb;
   	}

   	private static String readStreamFully(InputStream stream) throws IOException {
   		StringBuilder builder = new StringBuilder();
   		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
   		String response;
   		while ((response=reader.readLine())!=null) {
   			builder.append(response).append('\n');
   		}
   		return builder.toString();
   	}

    private static void writeOperationSuccessReport(Process p, String response) throws InterruptedException {
        int curlCode = p.waitFor();
        if (curlCode!=0) {
            System.err.println("Failed to submit result! Error code is " + curlCode);
        } else if (!response.trim().equals("created")) {
            System.err.println("Failed to submit result. Unexpected server response: " + response);
        } else {
            System.out.println("Result submitted.");
        }
    }
}
