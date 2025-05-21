package bg.sofia.uni.fmi.mjt.split.exception.server;

public class UserRepositoryException extends RepositoryException {
    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
