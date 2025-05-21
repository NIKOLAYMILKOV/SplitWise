package bg.sofia.uni.fmi.mjt.split.exception.user.group;

public class GroupAlreadyExistsException extends GroupException {
    public GroupAlreadyExistsException(String message) {
        super(message);
    }

    public GroupAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
