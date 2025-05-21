package bg.sofia.uni.fmi.mjt.split.exception.user.authentication;

public class UserNotLoggedInException extends AuthenticationException {
    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotLoggedInException(String message) {
        super(message);
    }
}
