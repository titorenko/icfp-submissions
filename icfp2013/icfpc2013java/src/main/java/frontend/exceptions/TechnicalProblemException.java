package frontend.exceptions;

/**
 *
 */
public class TechnicalProblemException extends RuntimeException {
    public TechnicalProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalProblemException(String message) {
        super(message);
    }
}
