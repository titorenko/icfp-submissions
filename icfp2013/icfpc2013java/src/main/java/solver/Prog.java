package solver;

import domain.Operator;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

public class Prog {
    private Operation operation;
    private boolean hasFold = false;
    private Collection<Operation> incompleteOperations = new HashSet<>();
    private int size = 0;

    public Prog() {
        operation = new Operation(Operator.lambda, false);
        incompleteOperations.add(operation);
    }

    public BigInteger evaluate(BigInteger arg) {
        if (!incompleteOperations.isEmpty()) {
            throw new IllegalStateException("trying evaluate incomplete programm");
        }
        BigInteger []context = new BigInteger[] { arg, null, null };
        return operation.evaluate(context);
    }

    public String toString() {
        return operation.toString();
    }

    public Operation getIncompleteOperation() {
        if (incompleteOperations.isEmpty()) {
            return null;
        }
        return incompleteOperations.iterator().next();
    }

    public boolean isComplete() {
        return incompleteOperations.isEmpty();
    }

    public void setParam(Operation operation, int param, Operation value) {
        operation.setParam(param, value);
        if (operation.isComplete()) {
            incompleteOperations.remove(operation);
        }
        if (!value.isComplete()) {
            incompleteOperations.add(value);
        }
        size ++;
    }

    public Prog clone() {
        Prog newProg = new Prog();
        newProg.operation = this.operation.clone();
        newProg.incompleteOperations = newProg.operation.getIncompleteParams();
        newProg.size = this.size;
        return newProg;
    }

    public int getSize() {
        return size;
    }

    public int getIncompleteOperatorsCount() {
        return incompleteOperations.size();
    }

    public Operation getRoot() {
        return operation.getParam(0);
    }
}
