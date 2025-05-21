package bg.sofia.uni.fmi.mjt.split.exception.user.registration;

public class UsernameTakenException extends RegistrationException {
    public UsernameTakenException(String message) {
        super(message);
    }

    public UsernameTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
