package icfp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class CurlHelper {

    public static String getSubmissionJson() {
        try {
            ProcessBuilder pb = prepareSubmitProcess();
            Process p = pb.start();
            String response = readStreamFully(p.getInputStream());
            writeOperationSuccessReport(p);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve data", e);
        }
    }

    private static ProcessBuilder prepareSubmitProcess() {
   		ProcessBuilder pb = new ProcessBuilder(
                   "curl",
                   "--user", ":w0agR+pfoRAqLcowQmQSmM8VbLuQGGvb4cdqqpv6A2Y=",
                   "-X", "GET",
                   "-ssl",
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

    private static void writeOperationSuccessReport(Process p) throws InterruptedException {
        int curlCode = p.waitFor();
        if (curlCode!=0) {
            throw new RuntimeException("Failed to retrieve document. Error code " + curlCode);
        }
    }
}
