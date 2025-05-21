package bg.sofia.uni.fmi.mjt.split.exception.user.friend;

public class UserAlreadyFriendsException extends FriendshipException {
    public UserAlreadyFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyFriendsException(String message) {
        super(message);
    }
}
