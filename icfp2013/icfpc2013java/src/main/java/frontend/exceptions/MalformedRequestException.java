package frontend.exceptions;

/**
 * We fucked up our request
 */
public class MalformedRequestException extends TechnicalProblemException {
    public MalformedRequestException(String message) {
        super(message);
    }
}
