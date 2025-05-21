package bg.sofia.uni.fmi.mjt.split.exception.user;

public class InvalidCommandFormatException extends Exception {
    public InvalidCommandFormatException(String message) {
        super(message);
    }

    public InvalidCommandFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
