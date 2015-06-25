package submission;

import domain.Operator;

import java.math.BigInteger;

public interface SolverPlugin {

    // search
    void doSearch(GuessVerifier verifier, String problemId, BigInteger[] inputs, BigInteger[] outputs, int size, Operator... ops);
}
