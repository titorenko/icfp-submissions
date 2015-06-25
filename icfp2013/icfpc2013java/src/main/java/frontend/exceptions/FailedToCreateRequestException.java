package frontend.exceptions;

/**
 *
 */
public class FailedToCreateRequestException extends TechnicalProblemException {
    public FailedToCreateRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
