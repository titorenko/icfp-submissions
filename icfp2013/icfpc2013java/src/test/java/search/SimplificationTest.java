package search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import domain.Identifier;
import domain.expression.IdExpression;
import domain.expression.LiteralExpression;
import domain.expression.ShrExpression;

public class SimplificationTest {
	@Test
	public void testSimplifyShr() {
		IdExpression id = new IdExpression(new Identifier("x"));
		ShrExpression e = new ShrExpression(new ShrExpression(id, 4), 1);
		assertEquals(new ShrExpression(id, 5), e.simplify());
		assertEquals(e, e.simplify().getCanonicalForm());
	}
	
	@Test
	public void testSimplifyShrConstant() {
		ShrExpression e = new ShrExpression(new ShrExpression(LiteralExpression.ONE, 4), 1);
		assertEquals("(shr1 (shr4 1))", e.simplify().toString());
		assertEquals(e, e.simplify().getCanonicalForm());
	}
}
