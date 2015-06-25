package util;

import domain.Operator;
import frontend.Frontend;
import frontend.Problem;
import java.util.*;

public class TaskFilter {

    private List<Problem> problems;

    public TaskFilter(Problem[] problems) {
        this.problems = new LinkedList<>(Arrays.asList(problems));
    }

    public void removeSolved() {
        Iterator<Problem> problemIterator = problems.iterator();
        while(problemIterator.hasNext()) {
            Problem problem = problemIterator.next();
            if (problem.getSolved()!=null) {
                problemIterator.remove();
            }
        }
    }

    public void excludeGreaterThan(int size) {
        Iterator<Problem> problemIterator = problems.iterator();
        while(problemIterator.hasNext()) {
            Problem problem = problemIterator.next();
            if (problem.getSize()>size)
                problemIterator.remove();
        }
    }

    public void excludeLessThan(int size) {
        Iterator<Problem> problemIterator = problems.iterator();
        while(problemIterator.hasNext()) {
            Problem problem = problemIterator.next();
            if (problem.getSize()<size)
                problemIterator.remove();
        }
    }

    public void excludeOperator(String op) {
        Iterator<Problem> problemIterator = problems.iterator();
        while(problemIterator.hasNext()) {
            Problem problem = problemIterator.next();
            if (hasOperator(problem, op))
                problemIterator.remove();
        }
    }

    private boolean hasOperator(Problem p, String badOp) {
        for(String op : p.getOperators()) {
            if (op.equals(badOp)) return true;
        }
        return false;
    }

    public void sort(Comparator<Problem> comparator) {
        Collections.sort(problems, comparator);
    }

    public Collection<Problem> getProblems() {
        return problems;
    }

    public static Problem[] fetchTasks() {
        Frontend frontend = new Frontend();
        return frontend.getProblems();
    }

    public static Problem[] readTasks() {
        return Util.getAllProblems();
    }

    public static void main(String[] args) {
        TaskFilter filter = new TaskFilter(fetchTasks());
        filter.excludeGreaterThan(12);
        filter.excludeLessThan(12);
//        filter.excludeOperator(Operator.fold.name());
//        filter.excludeOperator("bonus");
//        filter.excludeOperator(Operator.tfold.name());
//        filter.excludeOperator(Operator.if0.name());
        filter.removeSolved();
        filter.sort(new SizeComparator());
        for(Problem prob : filter.getProblems()) {
            System.out.println(prob);
        }
    }
}
