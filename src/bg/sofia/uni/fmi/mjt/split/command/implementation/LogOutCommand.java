package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;

public class LogOutCommand implements Command {
    private static final String LOG_OUT_MESSAGE = "Logged out";

    private final Authenticator authenticator;

    public LogOutCommand(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public String execute() {
        if (!authenticator.isAuthenticated()) {
            throw new UserNotLoggedInException(LOG_IN_REQUEST_MESSAGE);
        }
        authenticator.logout();
        return LOG_OUT_MESSAGE;
    }
}
