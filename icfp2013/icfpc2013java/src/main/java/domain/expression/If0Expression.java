package domain.expression;

import domain.Context;
import domain.Expression;

import java.math.BigInteger;

public class If0Expression extends Expression {

	private final Expression e0;
	private final Expression e1;
	private final Expression e2;

	public If0Expression(Expression e0, Expression e1, Expression e2) {
		this.e0 = e0;
		this.e1 = e1;
		this.e2 = e2;
	}

    @Override
   	public BigInteger evaluate(Context context) {
   		return e0.evaluate(context).equals(BigInteger.ZERO)?
   				e1.evaluate(context)
   				:
   				e2.evaluate(context);
   	}
    
    @Override
	public Expression simplify() {
		if (e0 instanceof LiteralExpression) {
			if (e0.equals(LiteralExpression.ZERO)) {
				return e1;
			} else {
				return e2;
			}
		}
		if (e0.equals(e1)) return e0;
		return this;
	}

	@Override
	public String toString() {
		String s0 = (e0.getCanonicalForm() != this) ? e0.getCanonicalForm().toString() : e0.toString();
		String s1 = (e1.getCanonicalForm() != this) ? e1.getCanonicalForm().toString() : e1.toString();
		String s2 = (e2.getCanonicalForm() != this) ? e2.getCanonicalForm().toString() : e2.toString();
		return "(if0 "+s0+" "+s1+" "+s2+")";
	}
}
