package domain.expression;

import domain.Context;
import domain.Expression;
import domain.Identifier;

import java.math.BigInteger;

/**
 *
 */
public class FoldExpression extends Expression {

    private static final BigInteger BYTE_MASK = new BigInteger("ff", 16);

    private final Identifier x;
    private final Identifier y;
    private final Expression valueToFold;
    private final Expression initialValue;
    private final Expression foldingFunction;

    public FoldExpression(Identifier x, Identifier y, Expression valueToFold,
                          Expression initialValue, Expression foldingFunction) {
        this.x = x;
        this.y = y;
        this.valueToFold = valueToFold;
        this.initialValue = initialValue;
        this.foldingFunction = foldingFunction;
    }

    public BigInteger evaluate(Context context) {
        context.setFold0Id(x);
        context.setFold1Id(y);
        BigInteger toEvaluate = valueToFold.evaluate(context);
        BigInteger accumulator = initialValue.evaluate(context);
        for(int i=0;i<8;i++) {
            context.setFold0Value(toEvaluate.and(BYTE_MASK));
            context.setFold1Value(accumulator);
            accumulator = foldingFunction.evaluate(context);
            toEvaluate = toEvaluate.shiftRight(8);
        }
        return accumulator;
   	}

    public String toString() {
        return "(fold "+valueToFold+" "+initialValue+" (lambda ("+x+" "+y+") "+foldingFunction+"))";

    }
}
