package frontend.exceptions;

/**
 *
 */
public class RetryLaterException extends RuntimeException {
    public RetryLaterException(String message) {
        super(message);
    }
}
