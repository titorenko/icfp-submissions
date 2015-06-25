package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;

import domain.expression.LiteralExpression;

public class ProgramTest {
	
	@Test
	public void testSimpleParse() {
		Program program = new Program("( lambda (x) 1 )");
		assertEquals("x", program.getId().toString());
		assertTrue("Was: "+program.getExpression(), program.getExpression() instanceof LiteralExpression);
		assertEquals(BigInteger.ONE, program.evaluate(BigInteger.TEN));
	}
	
	@Test
	public void testParseProblem5() {
		Program program = new Program("(lambda (x_4137) (shr4 (shr4 (shr1 x_4137))))");
		assertEquals("(shr4 (shr4 (shr1 x_4137)))", program.getExpression().toString());
	}
	

	@Test
	public void testParseProblem2() {
		Program program = new Program("(lambda (x_29275) "
				+   "(shr16 (shr4 (and (xor (shl1 (and 0 (if0 (shr16 (xor (and (xor x_29275 x_29275) 0) x_29275)) x_29275 0))) x_29275) x_29275))))");
		assertEquals("(shr16 (shr4 (and (xor (shl1 (and 0 (if0 (shr16 (xor (and (xor x_29275 x_29275) 0) x_29275)) x_29275 0))) x_29275) x_29275)))", program.getExpression().toString());
	}
	
	@Test
	@Ignore
	public void testEvalProblem2() {
		Program program = new Program("(lambda (x_29275) "
				+   "(shr16 (shr4 (and (xor (shl1 (and 0 (if0 (shr16 (xor (and (xor x_29275 x_29275) 0) x_29275)) x_29275 0))) x_29275) x_29275))))");
		BigInteger result = program.evaluate(new BigInteger("FFEFEEAA", 16));
		assertEquals("FFE", result.toString(16));
	}
	
	@Test
	public void testParseOleg() {
		Program program = new Program("(lambda (x_3822) (or (not x_3822) 0))");
		assertEquals("(or (not x_3822) 0)", program.getExpression().toString());
	}
	
	
	@Test
	public void testEvalNot0() {
		Program program = new Program("(lambda (x) (not x))");
		assertEquals("(or (not x_3822) 0)", program.evaluate(BigInteger.ZERO).toString(16).toUpperCase());
	}
	
	@Test
	public void testEvalProblem5() {
		Program program = new Program("(lambda (x_4137) (shr4 (shr4 (shr1 x_4137))))");
		assertEquals("32A", program.evaluate(new BigInteger("65535",16)).toString(16).toUpperCase());
		assertEquals("21B", program.evaluate(new BigInteger("43690",16)).toString(16).toUpperCase());
		assertEquals("214A4B39", program.evaluate(new BigInteger("4294967295",16)).toString(16).toUpperCase());
	}

    @Test
   	public void testEvalFold() {
   		Program program = new Program("(lambda (zzz) (fold zzz 0 (lambda (z a) (plus a 1))))");
   		assertEquals(new BigInteger("8"), program.evaluate(new BigInteger("fffffffaaa00", 16)));
   	}
}
