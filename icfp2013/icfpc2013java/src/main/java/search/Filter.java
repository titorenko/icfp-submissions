package search;

import domain.Context;
import domain.Expression;
import domain.Identifier;

import java.math.BigInteger;
import java.util.*;

public class Filter {

    private List<Expression> expressions;
    private Identifier inputName = new Identifier("x");

    public Filter(Collection<Expression> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public void addTestData(BigInteger input, BigInteger output) {
        Iterator<Expression> expressionIterator = expressions.iterator();
        while(expressionIterator.hasNext()) {
            Expression expr = expressionIterator.next();
            if (!output.equals(expr.evaluate(new Context(inputName, input)))) {
                expressionIterator.remove();
            }
        }
    }

    public boolean isEmpty() {
        return expressions.isEmpty();
    }

    public Expression getFirst() {
        return expressions.get(0);
    }

    public void dropFirst() {
        expressions.remove(0); // bloody expensive shit
    }
}
