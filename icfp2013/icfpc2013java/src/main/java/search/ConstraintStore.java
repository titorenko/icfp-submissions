package search;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.collect.ImmutableSet;

import domain.Context;
import domain.Expression;
import domain.Identifier;

public class ConstraintStore {
	private static final ImmutableSet<String> UNARY = ImmutableSet.of("not", "shl1", "shr1", "shr4", "shr16");
	private static final ImmutableSet<String> BINARY = ImmutableSet.of("and", "or", "xor", "plus");
	
	private List<BigInteger> arguments = new ArrayList<BigInteger>();
	private List<BigInteger> values = new ArrayList<BigInteger>();
	private final String[] unary;
	private final String[] binary;
	private int nIfs= 0;
	private boolean tfold = false;
	private boolean fold= false;
	boolean isFoldOnlyOutside = true;
	
	public ConstraintStore(String[] operators, BigInteger[] arguments, BigInteger[] values) {
		List<String> unary = new ArrayList<String>();
		List<String> binary = new ArrayList<String>();
		for (String op : operators) {
			if(isUnary(op)) {
				unary.add(op);
			} else if (isBinary(op)) {
				binary.add(op);
			} else if ("if0".equals(op)) {
				nIfs = 1;
			} else if ("tfold".equals(op)) {
				tfold = true;
			} else if ("fold".equals(op)) {
				fold = true;
			}
		}
		this.unary = unary.toArray(new String[0]);
		this.binary = binary.toArray(new String[0]);
		this.arguments = new ArrayList<BigInteger>(Arrays.asList(arguments));
		this.values = new ArrayList<BigInteger>(Arrays.asList(values));
		//this.selection = getRandomSelectionOfArgs(16);
	}
	
	public boolean isFoldOnlyOutside() {
		return isFoldOnlyOutside;
	}
	

	public ConstraintStore(List<BigInteger> arguments,  List<BigInteger> values, String[] unary, String[] binary, int nifs, boolean fold, boolean tfold
			, boolean isFoldOnlyOutside) {
		this.arguments = arguments;
		this.values = values;
		this.unary = unary;
		this.binary = binary;
		this.nIfs = nifs;
		this.fold = fold;
		this.tfold = tfold;
		this.isFoldOnlyOutside = isFoldOnlyOutside;
		//this.selection = getRandomSelectionOfArgs(16);
	}
	
	public List<BigInteger> getArguments() {
		return arguments;
	}
	
	/*public List<BigInteger> getSelection() {
		return selection;
	}*/
	
	/*private List<BigInteger> getRandomSelectionOfArgs(int n) {
		Random random = new Random();
		List<BigInteger> result = new ArrayList<>();
		for (int j = 0; j < n; j++) {
			BigInteger value = arguments.get(random.nextInt(arguments.size()));
			result.add(value);
		}
		return result;
	}*/
	
	
	public List<BigInteger> getValues() {
		return values;
	}

	private boolean isBinary(String op) {
		return BINARY.contains(op);
	}

	private boolean isUnary(String op) {
		return UNARY.contains(op);
	}

	public String[] getUnaryOperators() {
		return unary; 
	}

	public String[] getBinaryOperators() {
		return binary;
	}
	
	public int tryMoreIfs() {
		return nIfs++;
	}
	
	public int getIfs() {
		return nIfs;
	}

	public Set<Expression> filter(Set<Expression> expressions, Identifier id) {
		Set<Expression> result = new HashSet<Expression>();
		for (Expression expression : expressions) {
			if (isSatisfiable(expression, id)) {
				result.add(expression);
			}
		}
		return result;
	}

	public boolean isSatisfiable(Expression expression, Identifier id) {
		int index = 0;
		for (BigInteger argument : arguments) {
			Context newctx = Context.contextForPartialEvaluation(argument);
            BigInteger result = expression.evaluate(newctx);
			if (!result.equals(values.get(index))) return false;
			index++;
		}
		return true;
	}
	
	public boolean isTfold() {
		return tfold;
	}
	
	public boolean isFold() {
		return fold;
	}

	public boolean isHasIf0() {
		return nIfs > 0;
	}

	public ConstraintStore minusIf() {
		return new ConstraintStore(this.arguments, this.values, unary, binary, nIfs-1, fold, tfold, isFoldOnlyOutside);
	}

    public ConstraintStore minusFold() {
   		return new ConstraintStore(this.arguments, this.values, unary, binary, nIfs, false, false, isFoldOnlyOutside);
   	}

	public void add(Map<BigInteger, BigInteger> contrExamples) {
		for (Entry<BigInteger, BigInteger> ce : contrExamples.entrySet()) {
			arguments.add(ce.getKey());
			values.add(ce.getValue());
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
