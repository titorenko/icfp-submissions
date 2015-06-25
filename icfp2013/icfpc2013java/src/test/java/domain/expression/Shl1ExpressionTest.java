package domain.expression;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import domain.Context;
import domain.Identifier;

public class Shl1ExpressionTest {
	
	@Test
	public void testRegression() {
		Identifier x = new Identifier("x");
		ShlExpression e = new ShlExpression(new IdExpression(x), 1);
		BigInteger result = e.evaluate(new Context(x, new BigInteger("AAAAAAAAAA0000AA", 16)));
		assertEquals(new BigInteger("5555555554000154", 16).toString(16), result.toString(16));
	}
}
