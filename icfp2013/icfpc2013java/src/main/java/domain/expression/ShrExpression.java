package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;

public class ShrExpression extends Expression {

	private final Expression e;
	private final int shift;

	public ShrExpression(Expression e, int shift) {
		this.e = e;
		this.shift = shift;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return e.evaluate(context).shiftRight(shift);
	}
	
	@Override
	public Expression simplify() {
		if (e instanceof ShrExpression) {
			ShrExpression inner = (ShrExpression) e;
			ShrExpression result = new ShrExpression(inner.e, shift+inner.shift);
			return result;
		} 
		if (e instanceof LiteralExpression) {
			LiteralExpression inner = (LiteralExpression) e;
			return new LiteralExpression(inner.getValue().shiftRight(shift), this.getCanonicalForm());
		}
		return this;
	}
	
	@Override
	public Expression getCanonicalForm() {
		if (shift == 16 || shift == 4 || shift == 1) return this;
		int remainingShift = shift;
		Expression result = e;
		while(remainingShift >=16) {
			result = new ShrExpression(result, 16);
			remainingShift -= 16;
		}
		while(remainingShift >=4) {
			result = new ShrExpression(result, 4);
			remainingShift -= 4;
		}
		while(remainingShift >=1) {
			result = new ShrExpression(result, 1);
			remainingShift --;
		}
		return result;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + shift;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ShrExpression other = (ShrExpression) obj;
		if (shift != other.shift)
			return false;
		if (!e.equals(other.e))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (getCanonicalForm() == this) ? 
				"(shr"+shift+" "+e.toString()+")" : 
				getCanonicalForm().toString();
	}
}
