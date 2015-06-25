package frontend.exceptions;

/**
 *
 */
public class ProblemTimedOutException extends InvalidProblemException {
    public ProblemTimedOutException(String message, Object request) {
        super(message, request);
    }
}
