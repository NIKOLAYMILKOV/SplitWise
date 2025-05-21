package bg.sofia.uni.fmi.mjt.split.exception.user.registration;

public class UserNotRegisteredException extends RegistrationException {
    public UserNotRegisteredException(String message) {
        super(message);
    }

    public UserNotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
