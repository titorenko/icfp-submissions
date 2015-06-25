package solver;

import domain.Operator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.*;

public class BonusSolver {
    private final Set<Operator> operators;
    private final int size;
    private final List<BigInteger> xs;
    private final List<BigInteger> ys;

    BonusSolver(Set<Operator> operators, int size, List<BigInteger> xs, List<BigInteger> ys) {
        this.operators = new HashSet<>(operators);
        this.operators.add(Operator.one);
        this.operators.add(Operator.zero);
        this.operators.add(Operator.id0);
        this.operators.remove(Operator.fold);
        this.operators.remove(Operator.tfold);
        this.size = 9;
        this.xs = xs;
        this.ys = ys;
    }

    public Prog solve() {
        Set<String> vetoPrograms = new HashSet<>();
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
            Prog prog = progs.poll();
            for (Operator op : operators) {
                if (System.currentTimeMillis() - startTime > 5 * 60 * 1000) {
                    return null;
                }
                Prog newProg = prog.clone();
                Operation operation = newProg.getIncompleteOperation();
                int incompleteParamIndex = operation.getIncompleteParamIndex();
                newProg.setParam(operation, incompleteParamIndex, new Operation(op, false));
                if (operation.getOperator() == Operator.or && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.not && op == Operator.not) {
                    continue;
                }
                if (operation.getOperator() == Operator.and && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.xor && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.shl1 && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.shr1 && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.shr4 && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.shr16 && op == Operator.zero) {
                    continue;
                }
                if (newProg.isComplete()) {
                    if (isValidProg50pst(newProg, xs, ys) && !vetoPrograms.contains(newProg.toString())) {
                        Prog fullProg = solveSecondStage(newProg);
                        if (fullProg != null) {
                            return fullProg;
                        } else {
                            vetoPrograms.add(newProg.toString());
                        }
                    }
                } else {
                    if (newProg.getSize() + newProg.getIncompleteOperatorsCount() <= size + 1) {
                        progs.offer(newProg);
                    }
                }
            }
        }
        return null;
    }

    private Set<Integer> vetoedSubValues = new HashSet<>();

    public Prog solveSecondStage(Prog firstProg) {
        System.out.println("second stage: " + firstProg);
        Pair<List<BigInteger>, List<BigInteger>> missingXY = getMissedXY(firstProg);
        if (vetoedSubValues.contains(missingXY.getKey().size())) {
            return null;
        }
        vetoedSubValues.add(missingXY.getKey().size());
        List<BigInteger> xs = missingXY.getRight();
        List<BigInteger> ys = missingXY.getLeft();
        PriorityQueue<Prog> progs = new PriorityQueue<>(1000, new Comparator<Prog>() {
            @Override
            public int compare(Prog o1, Prog o2) {
                int v1 = o1.getSize() + o1.getIncompleteOperatorsCount();
                int v2 = o2.getSize() + o2.getIncompleteOperatorsCount();
                return v1 - v2;
            }
        });
        progs.offer(new Prog());
        long startTimstamp = System.currentTimeMillis();
        while (!progs.isEmpty()) {
            if (System.currentTimeMillis() - startTimstamp > 60 * 1000) {
                return null;
            }
            Prog prog = progs.poll();
            for (Operator op : operators) {
                Prog newProg = prog.clone();
                Operation operation = newProg.getIncompleteOperation();
                int incompleteParamIndex = operation.getIncompleteParamIndex();
                newProg.setParam(operation, incompleteParamIndex, new Operation(op, false));
                if (operation.getOperator() == Operator.or && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.not && op == Operator.not) {
                    continue;
                }
                if (operation.getOperator() == Operator.and && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.xor && op == Operator.zero) {
                    continue;
                }
                if (newProg.isComplete()) {
                    if (isValidProg(newProg, xs, ys)) {
                        Prog fullProg = solveThirdStage(firstProg, newProg);
                        if (fullProg != null) {
                            return fullProg;
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (newProg.getSize() + newProg.getIncompleteOperatorsCount() <= size + 1) {
                        progs.offer(newProg);
                    }
                }
            }
        }
        return null;
    }

    public Prog solveThirdStage(Prog firstProg, Prog secondProg) {
        System.out.println("third stage: " + firstProg + " " + secondProg);
        PriorityQueue<Prog> progs = new PriorityQueue<>(1000, new Comparator<Prog>() {
            @Override
            public int compare(Prog o1, Prog o2) {
                int v1 = o1.getSize() + o1.getIncompleteOperatorsCount();
                int v2 = o2.getSize() + o2.getIncompleteOperatorsCount();
                return v1 - v2;
            }
        });
        progs.offer(new Prog());
        long startTimstamp = System.currentTimeMillis();
        while (!progs.isEmpty()) {
            if (System.currentTimeMillis() - startTimstamp > 60*1000) {
                return null;
            }
            Prog prog = progs.poll();
            for (Operator op : operators) {
                Prog newProg = prog.clone();
                Operation operation = newProg.getIncompleteOperation();
                int incompleteParamIndex = operation.getIncompleteParamIndex();
                newProg.setParam(operation, incompleteParamIndex, new Operation(op, false));
                if (operation.getOperator() == Operator.or && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.not && op == Operator.not) {
                    continue;
                }
                if (operation.getOperator() == Operator.and && op == Operator.zero) {
                    continue;
                }
                if (operation.getOperator() == Operator.xor && op == Operator.zero) {
                    continue;
                }
                if (newProg.isComplete()) {
                    // build if0
                    Operation if0 = new Operation(Operator.if0, false);
                    if0.setParam(0, newProg.getRoot());
                    if0.setParam(1, firstProg.getRoot());
                    if0.setParam(2, secondProg.getRoot());
                    Prog resultProg = new Prog();
                    resultProg.setParam(resultProg.getIncompleteOperation(), 0, if0);
                    //System.out.println("candidate: " + resultProg);
                    if (isValidProg(resultProg, xs, ys)) {
                        return resultProg;
                    }
                } else {
                    if (newProg.getSize() + newProg.getIncompleteOperatorsCount() <= size + 1) {
                        progs.offer(newProg);
                    }
                }
            }
        }
        return null;
    }

    private static boolean isValidProg(Prog prog, List<BigInteger> xs, List<BigInteger> ys) {
        for (int i = 0; i < xs.size(); i++) {
            if (!ys.get(i).equals(prog.evaluate(xs.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidProg50pst(Prog prog, List<BigInteger> xs, List<BigInteger> ys) {
        int fails = 0;
        for (int i = 0; i < xs.size(); i++) {
            if (!ys.get(i).equals(prog.evaluate(xs.get(i)))) {
                fails++;
            }
            if (fails > xs.size() / 2 + 4) {
                return false;
            }
        }
        return true;
    }

    public Pair<List<BigInteger>, List<BigInteger>> getMissedXY(Prog prog) {
        Pair<List<BigInteger>, List<BigInteger>> result = new ImmutablePair<List<BigInteger>, List<BigInteger>>(
                new ArrayList<BigInteger>(), new ArrayList<BigInteger>());
        for (int i = 0; i < xs.size(); i++) {
            if (!ys.get(i).equals(prog.evaluate(xs.get(i)))) {
                result.getLeft().add(ys.get(i));
                result.getRight().add(xs.get(i));
            }
        }
        return result;
    }
}
