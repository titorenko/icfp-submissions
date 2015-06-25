package solver;

import domain.Operator;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Operation {
    public static final BigInteger ALL_BITS = new BigInteger("ffffffffffffffff", 16);
    private static final BigInteger BYTE_MASK = new BigInteger("ff", 16);

    final Operator operator;
    final Operation[] params;
    final boolean foldScope;

    public Operation(Operator operator, boolean foldScope) {
        this.operator = operator;
        this.params = new Operation[operator.getCardinality()];
        this.foldScope = foldScope;
    }

    public boolean isFoldScope() {
        return foldScope;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setParam(int i, Operation value) {
        params[i] = value;
    }

    public Operation getParam(int i) {
        return params[i];
    }

    public BigInteger evaluate(BigInteger[] context) {
        switch (operator) {
            case lambda:
                return params[0].evaluate(context);
            case id0:
                return context[0];
            case id1:
                return context[1];
            case id2:
                return context[2];
            case zero:
                return BigInteger.ZERO;
            case one:
                return BigInteger.ONE;
            case not:
                return params[0].evaluate(context).xor(ALL_BITS);
            case shl1:
                return params[0].evaluate(context).shiftLeft(1).and(ALL_BITS);
            case shr1:
                return params[0].evaluate(context).shiftRight(1);
            case shr4:
                return params[0].evaluate(context).shiftRight(4);
            case shr16:
                return params[0].evaluate(context).shiftRight(16);
            case and:
                return params[0].evaluate(context).and(params[1].evaluate(context));
            case or:
                return params[0].evaluate(context).or(params[1].evaluate(context));
            case xor:
                return params[0].evaluate(context).xor(params[1].evaluate(context));
            case plus:
                return params[0].evaluate(context).add(params[1].evaluate(context)).and(ALL_BITS);
            case if0:
                return params[0].evaluate(context).equals(BigInteger.ZERO) ?
                        params[1].evaluate(context) : params[2].evaluate(context);
            case fold: {
                BigInteger toEvaluate = params[0].evaluate(context);
                BigInteger accumulator = params[1].evaluate(context);
                for (int i = 0; i < 8; i++) {
                    context[1] = toEvaluate.and(BYTE_MASK);
                    context[2] = accumulator;
                    accumulator = params[2].evaluate(context);
                    toEvaluate = toEvaluate.shiftRight(8);
                }
                return accumulator;
            }
            case tfold: {
                BigInteger toEvaluate = context[0];
                BigInteger accumulator = BigInteger.ZERO;
                for (int i = 0; i < 8; i++) {
                    context[1] = toEvaluate.and(BYTE_MASK);
                    context[2] = accumulator;
                    accumulator = params[0].evaluate(context);
                    toEvaluate = toEvaluate.shiftRight(8);
                }
                return accumulator;
            }
            default:
                throw new UnsupportedOperationException("unsupported operator: " + operator.toString());
        }
    }

    public String toString() {
        switch (operator) {
            case lambda:
                return "(lambda (a0) " + params[0] + ")";
            case id0:
                return "a0";
            case id1:
                return "a1";
            case id2:
                return "a2";
            case zero:
                return "0";
            case one:
                return "1";
            case not:
                return "(not " + params[0] + ")";
            case shl1:
                return "(shl1 " + params[0] + ")";
            case shr1:
                return "(shr1 " + params[0] + ")";
            case shr4:
                return "(shr4 " + params[0] + ")";
            case shr16:
                return "(shr16 " + params[0] + ")";
            case and:
                return "(and " + params[0] + " " + params[1] + ")";
            case or:
                return "(or " + params[0] + " " + params[1] + ")";
            case xor:
                return "(xor " + params[0] + " " + params[1] + ")";
            case plus:
                return "(plus " + params[0] + " " + params[1] + ")";
            case if0:
                return "(if0 " + params[0] + " " + params[1] + " " + params[2] + ")";
            case fold:
                return "(fold " + params[0] + " " + params[1] + " (lambda (a1 a2) " + params[2] + "))";
            case tfold:
                return "(fold a0 0 (lambda (a1 a2) " + params[0] + "))";
            default:
                throw new UnsupportedOperationException("unsupported operator: " + operator.toString());
        }
    }

    public Operation clone() {
        Operation newOperation = new Operation(operator, foldScope);
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                newOperation.params[i] = params[i].clone();
            }
        }
        return newOperation;
    }

    public boolean isComplete() {
        for (Operation param : params) {
            if (param == null) {
                return false;
            }
        }
        return true;
    }

    public int getIncompleteParamIndex() {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                return i;
            }
        }
        throw new IllegalStateException("call getIncompleteParamIndex for complete operation");
    }

    public Set<Operation> getIncompleteParams() {
        Set<Operation> result = new HashSet<>();
        for (Operation param : params) {
            if (param != null) {
                result.addAll(param.getIncompleteParams());
            }
        }
        if (!isComplete()) {
            result.add(this);
        }
        return result;
    }
}
