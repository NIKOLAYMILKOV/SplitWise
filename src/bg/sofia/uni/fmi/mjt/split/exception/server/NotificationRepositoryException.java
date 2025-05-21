package bg.sofia.uni.fmi.mjt.split.exception.server;

public class NotificationRepositoryException extends RepositoryException {
    public NotificationRepositoryException(String message) {
        super(message);
    }

    public NotificationRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
