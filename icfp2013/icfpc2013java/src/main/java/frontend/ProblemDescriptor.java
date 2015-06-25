package frontend;

import domain.Operator;

import java.util.Set;

/**
 * Problem descriptor
 */
public interface ProblemDescriptor {
    public String getId();
    public int getSize();
    public String[] getOperators();
    public Set<Operator> getOperatorsSet();
}
