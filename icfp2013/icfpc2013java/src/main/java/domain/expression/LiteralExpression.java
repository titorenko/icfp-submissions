package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;

public class LiteralExpression extends Expression {

	public static LiteralExpression ZERO = new LiteralExpression(BigInteger.ZERO, null);
	public static LiteralExpression ONE = new LiteralExpression(BigInteger.ONE, null);
	
	private BigInteger value;
	
	public LiteralExpression(BigInteger value, Expression sourceExpression) {
		this.value = value;
		this.sourceExpression = sourceExpression;
	}
	
	public BigInteger getValue() {
		return value;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		LiteralExpression other = (LiteralExpression) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if (value.equals(BigInteger.ZERO) || value.equals(BigInteger.ONE)) 
			return value.toString();
		return getCanonicalForm().toString();
	}
}
