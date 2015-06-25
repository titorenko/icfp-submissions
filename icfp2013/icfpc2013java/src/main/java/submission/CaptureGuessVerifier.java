package submission;

import domain.Expression;

import java.math.BigInteger;
import java.util.Map;

/**
 * Captures first solution and reports ok
 */
public class CaptureGuessVerifier implements GuessVerifier {

    private Expression foundExpression;

    @Override
    public Map<BigInteger, BigInteger> findFailedData(Expression p) throws SubmissionFailedException, SubmissionSucceededException {
        foundExpression = p;
        throw new SubmissionSucceededException("Found solution");
    }

    public Expression getFoundExpression() {
        return foundExpression;
    }
}
