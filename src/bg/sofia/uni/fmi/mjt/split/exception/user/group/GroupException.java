package bg.sofia.uni.fmi.mjt.split.exception.user.group;

import bg.sofia.uni.fmi.mjt.split.exception.user.UserException;

public class GroupException extends UserException {
    public GroupException(String message) {
        super(message);
    }

    public GroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
