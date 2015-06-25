package scorer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.ConstraintStore;
import search.ProblemDefinitionParser;
import search.SearchEngine;
import search.TrainProblemDumper;
import submission.CaptureGuessVerifier;

import com.google.common.io.Files;

import domain.Expression;
import domain.Identifier;
import domain.Program;
import frontend.Frontend;
import frontend.Guess;
import frontend.GuessResponse;
import frontend.GuessResponseStatus;
import frontend.exceptions.ProblemAlreadySolvedException;

public class If0IntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(If0IntegrationTest.class);
	
    private CaptureGuessVerifier capture = new CaptureGuessVerifier();

	private Map<String, Pair<Integer, ConstraintStore>> getProblems() throws URISyntaxException, IOException {
		File root = new File(TrainProblemDumper.class.getResource("/train").toURI());
		File[] problems = root.listFiles((FileFilter)new WildcardFileFilter("*.txt"));
		Map<String, Pair<Integer, ConstraintStore>> result = new HashMap<>();
		for (int i = 0; i < problems.length; i++) {
			Pair<Integer, ConstraintStore> c = ProblemDefinitionParser.getConstraintsFor(problems[i].getName());
			String fname = problems[i].getName();
			String id = fname.substring(0, fname.lastIndexOf("."));
			result.put(id, c);
		}
		return result;
	}

	private int getScore() throws Exception {
		Map<String, Pair<Integer, ConstraintStore>> problems = getProblems();
		int nSolved = 0;
		int nNotSolved = 0;
		int totalTime = 0;
		for (Entry<String, Pair<Integer, ConstraintStore>> pair : problems.entrySet()) {
			int size = pair.getValue().getLeft();
			ConstraintStore cs = pair.getValue().getRight();
			
			if (size > 12) continue;
			if (cs.isFold() || cs.isTfold()) continue;
			if (!cs.isHasIf0()) continue;
			
			System.out.println("Solving "+pair.getKey());
			long start = System.currentTimeMillis();
            try {
			    new SearchEngine(new Identifier("x"), capture).search(size-1,cs);
                Expression result = capture.getFoundExpression();
    			verify(pair.getKey(), result);
                nSolved++;
            } catch (Exception ex) {
                nNotSolved++;
            }
			int duration = (int) (System.currentTimeMillis() - start);
			totalTime += duration;
		}
		System.out.println("Solved "+nSolved+"/"+nNotSolved+" in "+(totalTime)+" millis");
		return nSolved;
	}

	private void verify(String id, Expression expression) {
		Frontend frontend = new Frontend();
		Program program = new Program(new Identifier("x"), expression);
		if ("if0problem".equals(id)) return;
		System.out.println(id);
		System.out.println(program.toString());
		try {
			GuessResponse guess = frontend.guess(Guess.programGuess(id, program.toString()));
			if (guess.getStatus() == GuessResponseStatus.win) {
				return;
			}
		} catch (ProblemAlreadySolvedException e) {
			//ok
			return;
		}
		throw new RuntimeException("Failed to solve: "+id);
	}

	public void calculate() throws Exception {
		int score = getScore();
		LOG.info("Score: {}", score);
		File props = new File("score-plot.properties");
		Files.write("YVALUE = "+score, props, Charset.defaultCharset());
	}

	public static void main(String[] args) throws Exception {
		new If0IntegrationTest().calculate();
	}
}