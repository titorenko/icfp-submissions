package domain.expression;

import java.math.BigInteger;

import domain.Context;
import domain.Expression;
import domain.Identifier;

public class IdExpression extends Expression {

	private Identifier identifier;

	public IdExpression(Identifier identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public BigInteger evaluate(Context context) {
		return context.getValueOf(identifier);
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
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
		IdExpression other = (IdExpression) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	
	@Override
	public void setSourceExpression(Expression sourceExpression) {
		super.setSourceExpression(sourceExpression);
	}
}
