package bg.sofia.uni.fmi.mjt.split.exception.user.authentication;

public class InvalidUsernameOrPasswordException extends AuthenticationException {
    public InvalidUsernameOrPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUsernameOrPasswordException(String message) {
        super(message);
    }
}
