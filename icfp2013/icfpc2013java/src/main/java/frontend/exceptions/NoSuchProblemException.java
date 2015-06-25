package frontend.exceptions;

/**
 *
 */
public class NoSuchProblemException extends InvalidProblemException {
    public NoSuchProblemException(String message, Object request) {
        super(message, request);
    }
}
