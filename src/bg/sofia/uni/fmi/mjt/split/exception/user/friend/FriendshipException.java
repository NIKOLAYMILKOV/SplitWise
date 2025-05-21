package bg.sofia.uni.fmi.mjt.split.exception.user.friend;

public class FriendshipException extends RuntimeException {
    public FriendshipException(String message) {
        super(message);
    }

    public FriendshipException(String message, Throwable cause) {
        super(message, cause);
    }
}
