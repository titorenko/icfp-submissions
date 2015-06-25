package frontend;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import domain.Operator;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Arrays;
import java.util.Set;

/**
 * interface TrainingProblem {
 * challenge: string;
 * id: string;
 * size: number;
 * operators: string[];
 * }
 */
public class TrainingProblem implements ProblemDescriptor {
    private String challenge;
    private String id;
    private int size;
    private String[] operators;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String[] getOperators() {
        return operators;
    }

    @JsonIgnore
    public Set<Operator> getOperatorsSet() {
        if ("bonus".equals(operators[0])) {
            return Sets.newHashSet(
                    Operator.plus,Operator.shr1,Operator.xor,Operator.shr4,
                    Operator.and, Operator.if0, Operator.not, Operator.or,
                    Operator.shr16, Operator.shl1
            );
        }
        return Sets.newHashSet(
                Collections2.transform(Arrays.asList(operators), new Function<String, Operator>() {
                    @Override
                    public Operator apply(String input) {
                        return Operator.valueOf(input);
                    }
                }));
    }

    public void setOperators(String[] operators) {
        this.operators = operators;
    }

    @Override
    public String toString() {
        return "TrainingProblem{" +
                "challenge='" + challenge + '\'' +
                ", id='" + id + '\'' +
                ", size=" + size +
                ", operators=" + Arrays.toString(operators) +
                '}';
    }
}
