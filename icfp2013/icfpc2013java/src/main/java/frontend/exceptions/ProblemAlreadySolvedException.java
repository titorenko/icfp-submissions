package frontend.exceptions;

/**
 *
 */
public class ProblemAlreadySolvedException extends InvalidProblemException {
    public ProblemAlreadySolvedException(String message, Object request) {
        super(message, request);
    }
}
