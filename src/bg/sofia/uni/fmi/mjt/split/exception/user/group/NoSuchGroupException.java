package bg.sofia.uni.fmi.mjt.split.exception.user.group;

public class NoSuchGroupException extends GroupException {
    public NoSuchGroupException(String message) {
        super(message);
    }

    public NoSuchGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
