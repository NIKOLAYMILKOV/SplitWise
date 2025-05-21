package bg.sofia.uni.fmi.mjt.split.exception.user.authentication;

import bg.sofia.uni.fmi.mjt.split.exception.user.UserException;

public class AuthenticationException extends UserException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
