package bg.sofia.uni.fmi.mjt.split.exception.user.registration;

public class UserAlreadyRegisteredException extends RegistrationException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }

    public UserAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
