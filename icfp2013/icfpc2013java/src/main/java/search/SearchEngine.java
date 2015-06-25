package search;
import static search.SearchEngineMode.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import submission.GuessVerifier;
import submission.SolutionNotFoundExcepition;
import submission.SubmissionFailedException;
import submission.SubmissionSucceededException;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import domain.Context;
import domain.Expression;
import domain.Identifier;
import domain.expression.FoldExpression;
import domain.expression.IdExpression;
import domain.expression.If0Expression;
import domain.expression.LiteralExpression;

public class SearchEngine {

	private static final int TIME_BOUND = 5*60*1000;
	private final GuessVerifier verifier;
	private final Identifier initialId;
	private int maxSize;
	private final Identifier foldArgument;
    private final Identifier foldAccumulator;
    	
      // subtrees for set of identifiers
    // value is a map by size of expression
    public Map<ImmutableSet<Identifier>, Map<Integer, Set<Expression>>> expressionCache = new HashMap<>();
	private long searchStart;
	private SearchEngineMode mode;
	
	public SearchEngine(Identifier initialId, GuessVerifier verifier) {
		this(initialId, verifier, SearchEngineMode.BALANCED);
	}
	
	public SearchEngine(Identifier initialId, GuessVerifier verifier, SearchEngineMode mode) {
		this.initialId = initialId;
        this.foldArgument = new Identifier("y");
        this.foldAccumulator = new Identifier("a");
        this.verifier = verifier;
        this.searchStart = System.currentTimeMillis();
        this.mode = mode;
	}
	
	public void search(int size, ConstraintStore contraints) {
		this.maxSize = size;
		contraints.isFoldOnlyOutside = mode != PESSIMISTIC;
		try {
            getExpressions(size, ImmutableSet.of(initialId), contraints);
            throw new SolutionNotFoundExcepition("Can't find a solution in space.");
        } catch (SubmissionSucceededException e) {
            // good
        }
	}
	
	public Set<Expression> getExpressions(int size, ImmutableSet<Identifier> ids, ConstraintStore contraints) {
		checkTimeBound();
		if (expressionCache.get(ids) == null) {
			expressionCache.put(ids, new HashMap<Integer, Set<Expression>>());
		}
		if (expressionCache.get(ids).get(size) != null) {
			return expressionCache.get(ids).get(size);
		}
		Set<Expression> result = getExpressionsNonCached(size, ids, contraints);
        if (ids.size()==1 || mode == AGGRESSIVE) {
            // this means we only have input variable, but not fold (exps in fold cant be evaluated directly)
            Set<Expression> prunned =prune(result, ids, contraints);
            //System.out.println(result.size()+" -> "+prunned.size());
            result = prunned;
            if (ids.size()==1) checkIfAlreadyFound(result, contraints);
        }
        //System.out.println("Checked size "+size+" "+result.size());
        expressionCache.get(ids).put(size, result);
        return result;
		
	}

	private void checkTimeBound() {
		if ((System.currentTimeMillis() - searchStart) > TIME_BOUND) throw new SolutionNotFoundExcepition("");
	}

	private Set<Expression> prune(Set<Expression> expr, ImmutableSet<Identifier> ids, ConstraintStore contraints) {
		if (expr.size()>2_000_000 || (ids.size() >1 && mode != AGGRESSIVE) || mode == PESSIMISTIC) return expr;
		Set<Expression> result = new HashSet<>();
		Set<List<BigInteger>> values = new HashSet<>();
		for (Expression e: expr) {
			List<BigInteger> v = eval(contraints, ids, e);
			if (values.contains(v)) {
				continue;
			} else {
				values.add(v);
				result.add(e);
			}
			checkTimeBound();
		}
		return result;
	}
	
	BigInteger[] foldTestSet = new BigInteger[] {new BigInteger("5000F000F000F000",16), new BigInteger("FFFFFFF",16), new BigInteger("FAAAFFFFFFFFFF",16)};
	private List<BigInteger> eval(ConstraintStore contraints, ImmutableSet<Identifier> ids, Expression e) {
		List<BigInteger> arguments = contraints.getArguments();
		List<BigInteger> v = new ArrayList<>();
		Context newctx = Context.contextForPartialEvaluation(BigInteger.ZERO);
		if (ids.size() == 1) {
			for (BigInteger arg : arguments) {
				newctx.setInputValue(arg);
				v.add(e.evaluate(newctx));
			}
		} else {
			newctx.setFold0Id(new Identifier("y"));
			newctx.setFold1Id(new Identifier("a"));
			for (BigInteger arg1 : foldTestSet) {
				for (BigInteger arg2 : foldTestSet) {
					for (BigInteger acc: foldTestSet) {
						newctx.setInputValue(arg1);
						newctx.setFold0Value(arg2);
						newctx.setFold1Value(acc);
						v.add(e.evaluate(newctx));
					}
				}
			}
		}
		return v;
	}
	

    // exception of found solution or on technical failure
	private void checkIfAlreadyFound(Set<Expression> result, ConstraintStore contraints) {
		for (Expression e: result) {
			checkIfAlreadyFound(e, contraints);
			checkTimeBound();
		}
	}

	private void checkIfAlreadyFound(Expression e, ConstraintStore contraints) {
		if(contraints.isSatisfiable(e, initialId)) {
			Map<BigInteger, BigInteger> contrExamples;
			try {
				contrExamples = verifier.findFailedData(e);
			} catch (SubmissionFailedException e1) {
				System.err.println("Failed to submit, store "+e+" store:\n"+contraints);
				throw new RuntimeException("Failed to submit while doing a check.", e1);
			}
			contraints.add(contrExamples);
		}
	}

	private Set<Expression> getExpressionsNonCached(int size, ImmutableSet<Identifier> ids, ConstraintStore contraints) {
		if (size <= 0) return ImmutableSet.of();
		if (size == 1) return Sets.union(ImmutableSet.of(LiteralExpression.ZERO, LiteralExpression.ONE), getIdExpressions(ids));
		if (size == 2) return getUnary(getExpressions(size-1, ids, contraints), contraints);
		Set<Expression> union = new HashSet<Expression>();
		
		if (size==maxSize && size>=5 && contraints.isTfold()) {
            getTFold(size, ids, contraints, union);
        }
       
		if (size>=5 && contraints.isFold() && ((size == maxSize) || !contraints.isFoldOnlyOutside())) {
            getFold(size, ids, contraints, union);
        }
	        
		union.addAll(getUnary(getExpressions(size-1, ids, contraints), contraints));
		
		for(int i=1; i<= (size-1) /2; i++) {
			union.addAll(getBinary( getExpressions(size-1-i, ids, contraints), getExpressions(i, ids, contraints), contraints));
		}
		if (contraints.isHasIf0()) {
			for (int ifConditionComplexity = 1; ifConditionComplexity <= size-3; ifConditionComplexity++) {
				for(int ifComplexity=1; ifComplexity <= size-2-ifConditionComplexity;ifComplexity++) {
					for(int thenComplexity=1; thenComplexity<= size-1-(ifComplexity+ifConditionComplexity);thenComplexity++) {
						union.addAll(getIfs(
								getExpressions(ifConditionComplexity, ids, contraints.minusIf()), 
								getExpressions(ifComplexity, ids, contraints.minusIf()),  
								getExpressions(thenComplexity, ids, contraints.minusIf())));
					}
				}
			}
		}
      
		return union;
	}

	private void getFold(int size, ImmutableSet<Identifier> ids, ConstraintStore contraints, Set<Expression> union) {
		System.out.println("Doing fold");
		if (size > 11 && mode == PESSIMISTIC) size=11;
		if (size > 12 && mode == BALANCED) size=12;
		if (size > 13 && mode == AGGRESSIVE) size=13;
		ImmutableSet<Identifier> underFold = ImmutableSet.<Identifier>builder()
				.addAll(ids).add(foldArgument).add(foldAccumulator).build();
		for(int accExprSize = 1; accExprSize <= size-4; accExprSize++) {
			for(int foldExprSize = 1; foldExprSize <= size-3-accExprSize; foldExprSize++) {
		       for(int lambdaSize = 1; lambdaSize <= size-foldExprSize-accExprSize-2; lambdaSize++) {
		            union.addAll(getFolds(
		                    getExpressions(foldExprSize, ids, contraints.minusFold()),
		                    getExpressions(accExprSize, ids, contraints.minusFold()),
		                    getExpressions(lambdaSize, underFold, contraints.minusFold()),
		                    contraints
		            ));
		            checkTimeBound();
		       }
		    }
			System.out.println("Got folds for "+accExprSize);
		}
		System.out.println("Done fold");
	}

	private void getTFold(int size, ImmutableSet<Identifier> ids, ConstraintStore contraints, Set<Expression> union) {
		System.out.println("Doing tfold");
		if (size > 12 && mode == PESSIMISTIC) size=12;
		if (size > 14 && mode == BALANCED) size=14;
		if (size > 14 && mode == AGGRESSIVE) size=14;
        ImmutableSet<Identifier> underFold = ImmutableSet.<Identifier>builder()
                .addAll(ids).add(foldArgument).add(foldAccumulator).build();
		for(int foldExprSize = 1; foldExprSize <= size-4; foldExprSize++) {
		    for(int lambdaSize = 1; lambdaSize <= size-foldExprSize-3; lambdaSize++) {
		        union.addAll(getFolds(
		                getExpressions(foldExprSize, ids, contraints.minusFold()),
		                Sets.<Expression>newHashSet(LiteralExpression.ZERO),
		                getExpressions(lambdaSize, underFold, contraints.minusFold()),
		                contraints
		        ));
		        checkTimeBound();
		    }
		}
		System.out.println("Done tfold");
	}

    private Collection<Expression> getFolds(Set<Expression> fold, Set<Expression> initial, Set<Expression> lambda, ConstraintStore contraints) {
        List<Expression> result = new ArrayList<>();
        for (Expression c : fold) {
            for (Expression i : initial) {
                for (Expression t : lambda) {
                	FoldExpression foldexpr = new FoldExpression(foldArgument, foldAccumulator, c, i, t);
                	checkIfAlreadyFound(foldexpr, contraints);
                    if (!contraints.isFoldOnlyOutside()) result.add(foldexpr);
                }
                checkTimeBound();
            }
        }
        return result;
    }

	private Set<Expression> getIfs(Set<Expression> cond,
			Set<Expression> ifexpr, Set<Expression> thenexpr) {
		Set<Expression> result = new HashSet<>();
		for (Expression c : cond) {
			for (Expression i : ifexpr) {
				for (Expression t : thenexpr) {
					result.add(new If0Expression(c, i, t).simplify());
				}
			}
			checkTimeBound();
		}
		return result;
	}

	private ImmutableSet<Expression> getIdExpressions(ImmutableSet<Identifier> ids) {
		return FluentIterable.from(ids).transform(new Function<Identifier, Expression>() {
			@Override
			public Expression apply(Identifier input) {
				return new IdExpression(input);
			}
		}).toSet();
	}

	private Set<Expression> getBinary(Set<Expression> lhs, Set<Expression> rhs, ConstraintStore contraints) {
		if (lhs.isEmpty() || rhs.isEmpty()) return ImmutableSet.of();
		Set<Expression> result = new HashSet<Expression>();
		String[] operators = contraints.getBinaryOperators();
		for (String op : operators) {
			for (Expression l : lhs) {
				for (Expression r : rhs) {
					result.add(Expression.createBinary(op, l, r).simplify());
				}
			}
			checkTimeBound();
		}
		return result;
	}

	private Set<Expression> getUnary(Set<Expression> expressions, ConstraintStore contraints) {
		Set<Expression> result = new HashSet<Expression>();
		String[] operators = contraints.getUnaryOperators();
		for (String op : operators) {
			for (Expression expression : expressions) {
				result.add(Expression.createUnary(op, expression).simplify());
			}
			checkTimeBound();
		}
		return result;
	}
}
