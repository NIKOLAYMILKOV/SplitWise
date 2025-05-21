package bg.sofia.uni.fmi.mjt.split.exception.user.authentication;

public class AlreadyLoggedInException extends AuthenticationException {
    public AlreadyLoggedInException(String message) {
        super(message);
    }

    public AlreadyLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}
