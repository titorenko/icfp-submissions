package scorer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.ConstraintStore;
import search.ProblemDefinitionParser;
import search.SearchEngine;
import search.TrainProblemDumper;
import submission.CaptureGuessVerifier;
import submission.SolutionNotFoundExcepition;

import com.google.common.io.Files;

import domain.Identifier;

/**
 * It will be important to have one number which will estimate quality of our
 * implementation.
 */
public class ScoreCalculator {
	private static final Logger LOG = LoggerFactory.getLogger(ScoreCalculator.class);
	
    private CaptureGuessVerifier capture = new CaptureGuessVerifier();

	private Map<String, Pair<Integer, ConstraintStore>> getProblems() throws URISyntaxException, IOException {
		File root = new File(TrainProblemDumper.class.getResource("/").toURI());
		File[] problems = root.listFiles((FileFilter)new WildcardFileFilter("*.txt"));
		Map<String, Pair<Integer, ConstraintStore>> result = new HashMap<>();
		for (int i = 0; i < problems.length; i++) {
			Pair<Integer, ConstraintStore> c = ProblemDefinitionParser.getConstraintsFor(problems[i].getName());
			result.put(problems[i].getName(), c);
		}
		return result;
	}

	private int getScore() throws Exception {
		Map<String, Pair<Integer, ConstraintStore>> problems = getProblems();
		int nSolved = 0;
		int nNotSolved = 0;
		int totalTime = 0;
		Set<String> notSolved = new HashSet<String>();
		for (Entry<String, Pair<Integer, ConstraintStore>> pair : problems.entrySet()) {
			int size = pair.getValue().getLeft();
			ConstraintStore cs = pair.getValue().getRight();
		//	if (cs.isFold() || cs.isTfold()) continue;
			System.out.println("Solving "+pair.getKey());
			long start = System.currentTimeMillis();
            try {
			    new SearchEngine(new Identifier("x"), capture).search(Math.min(30,size-1), cs);
				nSolved++;
			} catch (SolutionNotFoundExcepition ex) {
				nNotSolved++;
				notSolved.add(pair.getKey());
			}
			int duration = (int) (System.currentTimeMillis() - start);
			totalTime += duration;
			System.out.println("Solved "+nSolved+"/"+nNotSolved+" in "+(totalTime)+" millis");
		}
		System.out.println("Not solved: "+notSolved);
		return nSolved;
	}

	public void calculate() throws Exception {
		int score = getScore();
		LOG.info("Score: {}", score);
		File props = new File("score-plot.properties");
		Files.write("YVALUE = "+score, props, Charset.defaultCharset());
	}

	public static void main(String[] args) throws Exception {
		new ScoreCalculator().calculate();
	}
}