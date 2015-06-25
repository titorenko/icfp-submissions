package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;

public class ShlExpression extends Expression {
	private final static BigInteger MASK = new BigInteger("FFFFFFFFFFFFFFFF",16);
	private final Expression e;
	private int shift;

	public ShlExpression(Expression e, int shift) {
		this.e = e;
		this.shift = shift;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return e.evaluate(context).shiftLeft(shift).and(MASK);
	}
	
	@Override
	public Expression simplify() {
		if (e instanceof ShlExpression) {
			ShlExpression inner = (ShlExpression) e;
			ShlExpression result = new ShlExpression(inner.e, shift+inner.shift);
			return result;
		} 
		if (e instanceof LiteralExpression) {
			LiteralExpression inner = (LiteralExpression) e;
			return new LiteralExpression(inner.getValue().shiftLeft(shift).and(MASK), this.getCanonicalForm());
		}
		return this;
	}
	
	@Override
	public Expression getCanonicalForm() {
		if (shift == 1) return this;
		int remainingShift = shift;
		Expression result = e;
		while(remainingShift >=1) {
			result = new ShlExpression(result, 1);
			remainingShift --;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + e.hashCode();
		result = prime * result + shift;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ShlExpression other = (ShlExpression) obj;
		if (shift != other.shift)
			return false;
		if (!e.equals(other.e))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (getCanonicalForm() == this) ? 
				"(shl"+shift+" "+e.toString()+")" : 
				getCanonicalForm().toString();
	}

}
