package bg.sofia.uni.fmi.mjt.split.exception.user.registration;

import bg.sofia.uni.fmi.mjt.split.exception.user.UserException;

public class RegistrationException extends UserException {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
