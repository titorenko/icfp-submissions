package search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

import submission.Submission;
import util.Util;
import frontend.EvalRequest;
import frontend.EvalResponse;
import frontend.Frontend;
import frontend.TrainRequest;
import frontend.TrainingProblem;

public class TrainProblemDumper {

	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		File root = new File("/home/costya/competitions/icfpc-2013/src/test/resources/train");
		final String[] inputs = Util.getTestDataPack();
		int nProblems = 1500;
		final Frontend frontend = new Frontend();
		for (int i = 0; i < nProblems; i++) {
			final TrainingProblem problem = frontend.retryTask(new Frontend.FrontendTask<TrainingProblem>() {
				@Override
		        public TrainingProblem doTask(Frontend frontend) {
					return  frontend.train(new TrainRequest());
		        }
		    });
			File file = new File(root, problem.getId().toString()+".txt");
			System.out.println(file);
			if (file.exists()) continue;
			String[] ops = problem.getOperators();
			int size = problem.getSize();
			String challenge = problem.getChallenge();
			EvalResponse response = frontend.retryTask(new Frontend.FrontendTask<EvalResponse>() {
				@Override
		        public EvalResponse doTask(Frontend frontend) {
					return  frontend.evaluate(EvalRequest.programRequestById(problem.getId(), inputs));
		        }
		    });
	        String[] outputs = response.getOutputs();
	        Properties properties = new Properties();
	        properties.setProperty("ops", Arrays.toString(ops));
	        properties.setProperty("size", ""+size);
	        properties.setProperty("x", Arrays.toString(inputs));
	        properties.setProperty("y", Arrays.toString(outputs));
	        properties.setProperty("solution", ""+challenge);
	        FileOutputStream fos = new FileOutputStream(file);
	        properties.store(fos, "");
	        fos.close();
	        System.out.println("saved");
	        Thread.sleep(3000);
		}
	}

}
