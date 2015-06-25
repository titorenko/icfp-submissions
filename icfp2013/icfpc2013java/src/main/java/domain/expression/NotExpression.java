package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;

public class NotExpression extends Expression {

    private static final BigInteger negator = new BigInteger("ffffffffffffffff", 16);

	private Expression e;

	public NotExpression(Expression e) {
		this.e = e;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return e.evaluate(context).xor(negator);
	}
	
	@Override
	public String toString() {
		String s0 = (e.getCanonicalForm() != this) ? e.getCanonicalForm().toString() : e.toString();
		return "(not "+s0+")";
	}
	
	@Override
	public Expression simplify() {
		if (e instanceof NotExpression) {
			NotExpression inner = (NotExpression) e;
			return inner.e;
		}
		if (e instanceof LiteralExpression) {
			LiteralExpression inner = (LiteralExpression) e;
			return new LiteralExpression(inner.getValue().xor(negator), this.getCanonicalForm());
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotExpression other = (NotExpression) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		return true;
	}
	
}