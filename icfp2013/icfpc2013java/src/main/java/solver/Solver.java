package solver;

import domain.Operator;

import java.math.BigInteger;
import java.util.*;

public class Solver {
    private final Set<Operator> operators;
    private final int size;
    private final List<BigInteger> xs;
    private final List<BigInteger> ys;

    Solver(Set<Operator> operators, int size, List<BigInteger> xs, List<BigInteger> ys) {
        this.operators = new HashSet<>(operators);
        this.operators.add(Operator.one);
        this.operators.add(Operator.zero);
        this.operators.add(Operator.id0);
        this.operators.add(Operator.id1);
        this.operators.add(Operator.id2);
        this.size = size;
        this.xs = xs;
        this.ys = ys;
    }

    public Prog solve() {
        PriorityQueue<Prog> progs = new PriorityQueue<>(1000, new Comparator<Prog>() {
            @Override
            public int compare(Prog o1, Prog o2) {
                int v1 = o1.getSize() + o1.getIncompleteOperatorsCount();
                int v2 = o2.getSize() + o2.getIncompleteOperatorsCount();
                return v1 - v2;
            }
        });
        progs.offer(new Prog());
        long startTime = System.currentTimeMillis();
        while (!progs.isEmpty()) {
            if (System.currentTimeMillis() - startTime > 5 * 60 * 1000) {
                return null;
            }
            Prog prog = progs.poll();
            for (Operator op : operators) {
                Prog newProg = prog.clone();
                Operation operation = newProg.getIncompleteOperation();
                int incompleteParamIndex = operation.getIncompleteParamIndex();
                boolean isInFoldScope = operation.isFoldScope() ||
                        (operation.getOperator() == Operator.fold && incompleteParamIndex == 2) ||
                        operation.getOperator() == Operator.tfold;
                if (isInFoldScope && (op ==  Operator.fold || op == Operator.tfold)) {
                    continue;
                }
                if (op == Operator.tfold && operation.getOperator() != Operator.lambda) {
                    continue;
                }
                if (isInFoldScope || op != Operator.id1 && op != Operator.id2) {
                    newProg.setParam(operation, incompleteParamIndex, new Operation(op, isInFoldScope));
                    if (newProg.isComplete()) {
                        if (isValidProg(newProg)) {
                            return newProg;
                        }
                    } else {
                        if (newProg.getSize() <= size + 1) {
                            progs.offer(newProg);
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isValidProg(Prog prog) {
        for (int i = 0; i < xs.size(); i++) {
            if (!ys.get(i).equals(prog.evaluate(xs.get(i)))) {
                return false;
            }
        }
        return true;
    }
}
