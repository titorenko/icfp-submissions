package domain;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Program {
	private static final String regex = "^\\(\\s*lambda\\s+\\((\\p{Alpha}[\\p{Alnum}_]*)\\)\\s*(.+)\\)$";
	private static final Pattern pattern = Pattern.compile(regex);
	
	private Expression e;
	private Identifier id;
	
	public Program(String representation) {
		Matcher matcher = pattern.matcher(representation);
		if (matcher.matches()) {
            id = new Identifier(matcher.group(1).trim());
			e = Expression.fromString(matcher.group(2).trim());
		} else {
			throw new IllegalArgumentException("Bad input: "+representation);
		}
	}

	public Program(Identifier identifier, Expression expression) {
		this.id = identifier;
		this.e = expression;
	}

	public Expression getExpression() {
		return e;
	}

	public BigInteger evaluate(BigInteger input) {
		return e.evaluate(new Context(id, input));
	}

    public Identifier getId() {
        return id;
    }
    
    @Override
    public String toString() {
    	return "(lambda ("+id+") " + e.toString() + ")";
    }
}
