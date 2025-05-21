package bg.sofia.uni.fmi.mjt.split.exception.server;

public class GroupRepositoryException extends RepositoryException {
    public GroupRepositoryException(String message) {
        super(message);
    }

    public GroupRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
