package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;

public class XorExpression extends Expression {

	private final Expression e0;
	private final Expression e1;

	public XorExpression(Expression e0, Expression e1) {
		this.e0 = e0;
		this.e1 = e1;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return e0.evaluate(context).xor(e1.evaluate(context));
	}
	
	@Override
	public Expression simplify() {
		if (e0 instanceof LiteralExpression && e1 instanceof LiteralExpression) {
			LiteralExpression left = (LiteralExpression) e0;
			LiteralExpression right = (LiteralExpression) e1;
			return new LiteralExpression(left.getValue().xor(right.getValue()), this.getCanonicalForm());
		}
		if (e0 instanceof LiteralExpression) {
			LiteralExpression left = (LiteralExpression) e0;
			if (left.getValue().equals(BigInteger.ZERO)) {
				return e1;
			}
		}
		if (e1 instanceof LiteralExpression) {
			LiteralExpression right = (LiteralExpression) e1;
			if (right.getValue().equals(BigInteger.ZERO)) {
				return e0;
			}
		}
		if (e0.equals(e1)) {
			return LiteralExpression.ZERO;
		}
		
		return this;
	}
	
	@Override
	public int hashCode() {
		return e0.hashCode()+e1.hashCode()+13456;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		XorExpression other = (XorExpression) obj;
		if (e0.equals(other.e0) && e1.equals(other.e1)) return true;
		if (e1.equals(other.e0) && e0.equals(other.e1)) return true;
		return false;
	}
	
	
	@Override
	public String toString() {
		String s0 = (e0.getCanonicalForm() != this) ? e0.getCanonicalForm().toString() : e0.toString();
		String s1 = (e1.getCanonicalForm() != this) ? e1.getCanonicalForm().toString() : e1.toString();
		return "(xor "+s0+" "+s1+")";
	}
}
