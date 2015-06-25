package search;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import domain.Identifier;
import submission.CaptureGuessVerifier;

public class SearchEngineTest {

    private CaptureGuessVerifier capture = new CaptureGuessVerifier();

    @Test
	public void testProblem5() {
		ConstraintStore constraintStore = new ConstraintStore(
				new String[] {"shr1","shr4"},
				new BigInteger[] {new BigInteger("4294967295",16),new BigInteger("65535",16),new BigInteger("4293914282",16),new BigInteger("43690",16)},
				new BigInteger[] {new BigInteger("214A4B39",16),new BigInteger("32A",16),new BigInteger("2149C8A1",16),new BigInteger("21B",16)}
				);
		new SearchEngine(new Identifier("x"), capture).search(4,constraintStore);
//		assertEquals(1,result.size());
		assertEquals("(shr1 (shr4 (shr4 x)))",capture.getFoundExpression().toString());
	}
	
	@Test
	public void testProblem2() {
		ConstraintStore constraintStore = new ConstraintStore(
			new String[] {"and","if0","shl1","shr16","shr4","xor"},
			new BigInteger[] {i("AAAAFFFFFFFF"),i("AAAA"),i("FFEFEEAA"),i("FFFFFFFFFFFFFFFF")},
			new BigInteger[] {i("AAAAFFF"),i("0"),i("FFE"),i("FFFFFFFFFFF")}
		);
		new SearchEngine(new Identifier("x"), capture).search(8,constraintStore);
		System.out.println(capture.getFoundExpression().toString());
	}
	
	@Test
	public void testIfProblem() throws IOException {
		Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("if0problem.txt");
		new SearchEngine(new Identifier("x"), capture).search(cs.getLeft(),cs.getRight());
		System.out.println(capture.getFoundExpression().toString());
	}

    @Test@Ignore
   	public void testFoldProblem() throws IOException {
   		Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("grS3aISwyRkbKeDVmacUvpAz.txt");
   		new SearchEngine(new Identifier("x"), capture).search(cs.getLeft(),cs.getRight());
   		System.out.println(capture.getFoundExpression().toString());
   	}

    @Test
    public void testProblem9Real() throws IOException {
    	Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("grS3aISwyRkbKeDVmacUvpAz.txt");
   		new SearchEngine(new Identifier("x"), capture).search(cs.getLeft(),cs.getRight());
   		assertEquals("(fold x 0 (lambda (y a) (if0 y 1 y)))",capture.getFoundExpression().toString());
    }
    
    @Test
    public void testProblemFold15Real() throws IOException {
    	Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("6rqVuVNO93BlOixyHwS8xDq4.txt");
   		new SearchEngine(new Identifier("x"), capture).search(cs.getLeft(),cs.getRight());
   		System.out.println(capture.getFoundExpression().toString());
    }
    
    @Test
    public void testProblem30() throws IOException {
    	Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("hugLPF51AZPcsAsxdkUDg94q.txt");
   		new SearchEngine(new Identifier("x"), capture).search(cs.getLeft(),cs.getRight());
   		System.out.println(capture.getFoundExpression().toString());
    }
    
    @Test
    public void testFold15() throws IOException {
    	//Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("fj5XztK9SMTdjFJunZQOZDgB.txt");
    	//Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("LPl6ZXSpPglK6DAGZ2AAdlTA.txt");
    	Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("WTY3Vc5WnN8jwy0aieGCnxw8.txt");
    	//Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("5wYVXxdwkWjOZy5FdiUfrH99.txt");
    	//Pair<Integer, ConstraintStore> cs = ProblemDefinitionParser.getConstraintsFor("Pxjxk6CCtHmdKBz4hRN62Qlg.txt");
   		new SearchEngine(new Identifier("x"), capture, SearchEngineMode.PESSIMISTIC).search(cs.getLeft(),cs.getRight());
   		System.out.println(capture.getFoundExpression().toString());
    	
    }
    
    
    
	private BigInteger i(String repr) {
		return new BigInteger(repr,16);
	}
	
}
