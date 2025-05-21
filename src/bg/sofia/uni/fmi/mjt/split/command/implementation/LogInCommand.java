package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.AlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.InvalidUsernameOrPasswordException;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;

public class LogInCommand implements Command {
    private static final String SUCCESSFUL_LOG_IN_MESSAGE = "Welcome %s %s \n %s";
    private static final String ALREADY_LOGGED_IN_MESSAGE = "You are already logged in.";
    private static final String INVALID_LOG_IN_INFORMATION_MESSAGE = "Invalid username or password.";


    private static final int TOKENS_COUNT = 2;
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    private final Authenticator authenticator;
    private final NotificationRepository notificationRepository;
    private final String[] tokens;

    public LogInCommand(Authenticator authenticator, NotificationRepository notificationRepository, String[] tokens) {
        this.authenticator = authenticator;
        this.notificationRepository = notificationRepository;
        this.tokens = tokens;
    }

    @Override
    public String execute() throws InvalidCommandFormatException {
        if (authenticator.isAuthenticated()) {
            throw new AlreadyLoggedInException(ALREADY_LOGGED_IN_MESSAGE);
        }
        validateTokens();
        boolean authenticated = authenticator.login(tokens[USERNAME_INDEX], tokens[PASSWORD_INDEX]);
        if (!authenticated) {
            throw new InvalidUsernameOrPasswordException(INVALID_LOG_IN_INFORMATION_MESSAGE);
        }
        User user = authenticator.getAuthenticatedUser();

        return String.format(SUCCESSFUL_LOG_IN_MESSAGE, user.firstName(), user.lastName(),
            notificationRepository.notify(tokens[USERNAME_INDEX]));
    }

    void validateTokens() throws InvalidCommandFormatException {
        if (tokens.length < TOKENS_COUNT) {
            throw new InvalidCommandFormatException(Command.INVALID_FORMAT_MESSAGE);
        }
    }
}
