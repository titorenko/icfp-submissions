package submission;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

import domain.Expression;
import domain.Program;

public interface GuessVerifier {

	GuessVerifier NONE = new GuessVerifier() {

		@Override
		public Map<BigInteger, BigInteger> findFailedData(Expression p) {
            throw new SubmissionSucceededException("Test submission ok");
		}
	};
	
	/** return contr examples or empty if correct*/
    /**
     *
     * @param p program to check
     * @return
     * @throws SubmissionFailedException if submission fails for technical reasons
     * @throws SubmissionSucceededException if submission succeeds
     */
	public Map<BigInteger, BigInteger> findFailedData(Expression p) throws SubmissionFailedException, SubmissionSucceededException;

}
