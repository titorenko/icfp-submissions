package parser;

public class ParsingException extends RuntimeException {
    public ParsingException(String message, int lineNumber) {
        super(message + " @ " + lineNumber);
    }

    public ParsingException(String message, int lineNumber, Throwable cause) {
        super(message + " @ " + lineNumber, cause);
    }
}
