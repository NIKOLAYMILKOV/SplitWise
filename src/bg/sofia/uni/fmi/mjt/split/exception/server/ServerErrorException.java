package bg.sofia.uni.fmi.mjt.split.exception.server;

public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
