package frontend.exceptions;

import frontend.ProblemRequest;

public class InvalidProblemException extends RuntimeException {
    public InvalidProblemException(String message, Object request) {
        super(message + (request instanceof ProblemRequest?((ProblemRequest) request).getId():"unknown"));
    }
}
