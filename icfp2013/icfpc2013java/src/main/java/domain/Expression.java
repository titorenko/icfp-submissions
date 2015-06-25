package domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import domain.expression.AndExpression;
import domain.expression.FoldExpression;
import domain.expression.IdExpression;
import domain.expression.If0Expression;
import domain.expression.LiteralExpression;
import domain.expression.NotExpression;
import domain.expression.OrExpression;
import domain.expression.PlusExpression;
import domain.expression.ShlExpression;
import domain.expression.ShrExpression;
import domain.expression.XorExpression;

public abstract class Expression {
    private static final String LAMBDA_REGEX = "^\\(\\s*lambda\\s+\\((\\p{Alpha}[\\p{Alnum}_]*)\\s+(\\p{Alpha}[\\p{Alnum}_]*)\\)\\s*(\\(.+\\))\\s*\\)$";
   	private static final Pattern LAMBDA_PATTERN = Pattern.compile(LAMBDA_REGEX);

    public static final BigInteger ALL_BITS = new BigInteger("ffffffffffffffff", 16);

	private static final String UNARY_REGEX = "\\(\\s*(not|shr16|shl1|shr1|shr4)\\s*(.+)\\)";
	private static final Pattern UNARY_PATTERN = Pattern.compile(UNARY_REGEX);
	
	private static final String BINARY_REGEX = "\\(\\s*(and|or|xor|plus)\\s*(.+)\\)";
	private static final Pattern BINARY_PATTERN = Pattern.compile(BINARY_REGEX);
	
	private static final String IF_REGEX = "\\(\\s*if0\\s*(.+)\\)";
	private static final Pattern IF_PATTERN = Pattern.compile(IF_REGEX);
	
    private static final String FOLD_REGEX = "\\(\\s*fold\\s*(.+)\\)";
   	private static final Pattern FOLD_PATTERN = Pattern.compile(FOLD_REGEX);

	private static final String ID_REGEX = "\\p{Lower}[\\p{Lower}\\p{Digit}_]*";
	private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);
	private static final Pattern ID_PREFIX_PATTERN = Pattern.compile("("+ID_REGEX+").*");
	
	protected Expression sourceExpression;
	
	public static Expression fromString(String representation) {
		if(representation.equals("1")) {
			return LiteralExpression.ONE;
		} else if (representation.equals("0")) {
			return LiteralExpression.ZERO;
		} else if (ID_PATTERN.matcher(representation).matches()){
			return new IdExpression(new Identifier(representation));
		} else  {
			return parseOperation(representation);
		}
	}
	
	private static Expression parseOperation(String representation) {
		Matcher matcher = UNARY_PATTERN.matcher(representation);
		if (matcher.matches()) {
			return createUnary(matcher.group(1), Expression.fromString(matcher.group(2)));
		}
		matcher = BINARY_PATTERN.matcher(representation);
		if (matcher.matches()) {
			String[] expressions = splitExpressions(matcher.group(2));
			Preconditions.checkArgument(expressions.length == 2);
			return createBinary(matcher.group(1),
                    Expression.fromString(expressions[0]),
                    Expression.fromString(expressions[1]));
		}
		matcher = IF_PATTERN.matcher(representation);
		if (matcher.matches()) {
			String[] expressions = splitExpressions(matcher.group(1));
			Preconditions.checkArgument(expressions.length == 3);
			return new If0Expression(
					Expression.fromString(expressions[0]),
					Expression.fromString(expressions[1]),
					Expression.fromString(expressions[2]));
		}
        matcher = FOLD_PATTERN.matcher(representation);
        if (matcher.matches()) {
            String[] expressions = splitExpressions(matcher.group(1));
            Preconditions.checkArgument(expressions.length == 3);
            // dirtu hack to make toString work for lambda var binding
            LambdaDesc lambda = prepareLambda(expressions[2]);
            return new FoldExpression(
                    lambda.x,
                    lambda.y,
                    Expression.fromString(expressions[0]),
                    Expression.fromString(expressions[1]),
                    Expression.fromString(lambda.expression)
            );
        }
		throw new IllegalArgumentException("Can not parse op expr: "+representation);
	}

    // extract formal parameters into context as part of string extraction
    private static LambdaDesc prepareLambda(String string) {
        Matcher matcher = LAMBDA_PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Can not parse lambda: " + string);
        }
        return new LambdaDesc(new Identifier(matcher.group(1)), new Identifier(matcher.group(2)), matcher.group(3));
    }

    private static String[] splitExpressions(String string) {
		List<String> result = new ArrayList<String>();
		int index = 0;
		String expr = null;
		do {
			expr = getNextExpression(string, index);
			if (expr != null) {
				result.add(expr);
				index += expr.length()+1;
			}
		} while(expr != null);
		return result.toArray(new String[0]);
	}

	private static String getNextExpression(String string, int index) {
		if (index>=string.length()) return null;
		String suffix = string.substring(index).trim();
		char start = suffix.charAt(0);
		if (start == '0') {
			return "0";
		} else if (start == '1') {
			return "1";
		} else if (start >= 'a' && start <= 'z') {
			Matcher matcher = ID_PREFIX_PATTERN.matcher(suffix);
			Preconditions.checkArgument(matcher.matches());
			return matcher.group(1);
		} else if (start == '('){
			char[] charArray = suffix.toCharArray();
			int parenCount = 0;
			int endIndex = 0;
			for (; endIndex < charArray.length; endIndex++) {
				if (charArray[endIndex] == '(') {
					parenCount++;
				} else if (charArray[endIndex] == ')') {
					parenCount--;
				}
				if (parenCount == 0) {
					endIndex++;
					break;
				}
			}
			return suffix.substring(0, endIndex);
		}
		return null;
	}

	public static Expression createBinary(String operation, Expression e0, Expression e1) {
		switch (operation) {
		case "and": return new AndExpression(e0, e1);
		case "or": return new OrExpression(e0, e1);
		case "xor": return new XorExpression(e0, e1);
		case "plus": return new PlusExpression(e0, e1);
		default: throw new IllegalArgumentException("Unknown op: "+operation);
		}
	}

	public static Expression createUnary(String operation, Expression e) {
		switch (operation) {
		case "not": return new NotExpression(e);
		case "shl1": return new ShlExpression(e, 1);
		case "shr1": return new ShrExpression(e, 1);
		case "shr4": return new ShrExpression(e, 4);
		case "shr16": return new ShrExpression(e, 16);
		default: throw new IllegalArgumentException("Unknown op: "+operation);
		}
	}
	
	public void setSourceExpression(Expression sourceExpression) {
		this.sourceExpression = sourceExpression;
	}

	public BigInteger evaluate(Context context) {
		throw new UnsupportedOperationException("not yet implemented for: "+getClass());
	}

	public Expression simplify() {
		return this;
	}
	
	public Expression getCanonicalForm() {
		return sourceExpression == null ? this : sourceExpression.getCanonicalForm();
	}


    private static class LambdaDesc {
        final Identifier x;
        final Identifier y;
        final String expression;

        private LambdaDesc(Identifier x, Identifier y, String expression) {
            this.x = x;
            this.y = y;
            this.expression = expression;
        }
    }
}
