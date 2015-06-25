package domain.expression;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import domain.Context;
import domain.Identifier;

public class Shr1ExpressionTest {
	@Test
	public void testSimpleEvaluation() {
		Identifier x = new Identifier("x");
		ShrExpression shr1 = new ShrExpression(new IdExpression(x), 1);
		BigInteger result = shr1.evaluate(new Context(x, BigInteger.valueOf(0b101L)));
		assertEquals(BigInteger.valueOf(0b10L), result);
	}
	
	@Test
	public void testBoundaryEval() {
		Identifier x = new Identifier("x");
		ShrExpression shr1 = new ShrExpression(new IdExpression(x), 1);
		BigInteger result = shr1.evaluate(new Context(x, new BigInteger("FFFFFFFFFFFFFFFF", 16)));
		assertEquals(new BigInteger("7FFFFFFFFFFFFFFF", 16), result);
	}
}
