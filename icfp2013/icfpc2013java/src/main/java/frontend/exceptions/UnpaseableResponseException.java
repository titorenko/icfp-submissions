package frontend.exceptions;

/**
 * Don't know how to handle response
 */
public class UnpaseableResponseException extends TechnicalProblemException {
    public UnpaseableResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
