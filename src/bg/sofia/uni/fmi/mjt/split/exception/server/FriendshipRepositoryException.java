package bg.sofia.uni.fmi.mjt.split.exception.server;

public class FriendshipRepositoryException extends RepositoryException {
    public FriendshipRepositoryException(String message) {
        super(message);
    }

    public FriendshipRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
