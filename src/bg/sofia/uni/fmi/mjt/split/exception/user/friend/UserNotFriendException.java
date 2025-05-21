package bg.sofia.uni.fmi.mjt.split.exception.user.friend;

public class UserNotFriendException extends FriendshipException {
    public UserNotFriendException(String message) {
        super(message);
    }

    public UserNotFriendException(String message, Throwable cause) {
        super(message, cause);
    }
}
