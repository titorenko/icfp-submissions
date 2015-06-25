package scorer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.tuple.Pair;

import search.ConstraintStore;
import search.ProblemDefinitionParser;
import search.SearchEngine;
import search.TrainProblemDumper;
import submission.CaptureGuessVerifier;
import domain.Identifier;

public class If0AnotherTest {
	
    private CaptureGuessVerifier capture = new CaptureGuessVerifier();

	private void runProblem() throws URISyntaxException, IOException {
		File root = new File(TrainProblemDumper.class.getResource("/").toURI());
		File problem = new File(root, "RKrJwnGhqSgA0NkrqzY1FkDv.txt");
		Pair<Integer, ConstraintStore> c = ProblemDefinitionParser.getConstraintsFor(problem.getName());
		String fname = problem.getName();
		String id = fname.substring(0, fname.lastIndexOf("."));
		new SearchEngine(new Identifier("x"), capture).search(c.getLeft(), c.getRight());
		System.out.println(capture.getFoundExpression());
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		new If0AnotherTest().runProblem();
	}
}
