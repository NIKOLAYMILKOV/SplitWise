package bg.sofia.uni.fmi.mjt.split.exception.user.group;

public class UserNotGroupMemberException extends RuntimeException {
    public UserNotGroupMemberException(String message) {
        super(message);
    }

    public UserNotGroupMemberException(String message, Throwable cause) {
        super(message, cause);
    }

}
