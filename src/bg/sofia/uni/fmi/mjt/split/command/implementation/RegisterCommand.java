package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.encryption.PasswordHashUtil;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

public class RegisterCommand implements Command {
    private static final String SUCCESSFUL_REGISTRATION_MESSAGE = "%s %s registered successfully";
    private static final String ALREADY_REGISTERED_USER_MESSAGE = "%s %s is already registered";

    private static final int TOKENS_COUNT = 4;
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private static final int FIRST_NAME_INDEX = 2;
    private static final int LAST_NAME_INDEX = 3;

    private final UserRepository userRepository;
    private final String[] tokens;

    public RegisterCommand(UserRepository userRepository, String[] tokens) {
        this.userRepository = userRepository;
        this.tokens = tokens;
    }

    @Override
    public String execute() throws InvalidCommandFormatException {
        validate(tokens);
        User user = new User(tokens[USERNAME_INDEX], PasswordHashUtil.hashPassword(tokens[PASSWORD_INDEX]),
            tokens[FIRST_NAME_INDEX], tokens[LAST_NAME_INDEX]);
        boolean isRegistered = userRepository.register(user);
        if (!isRegistered) {
            throw new UserAlreadyRegisteredException(
                String.format(ALREADY_REGISTERED_USER_MESSAGE, user.firstName(), user.lastName()));
        }
        userRepository.save();
        return String.format(SUCCESSFUL_REGISTRATION_MESSAGE, user.firstName(), user.lastName());
    }

    void validate(String[] tokens) throws InvalidCommandFormatException {
        if (tokens.length < TOKENS_COUNT) {
            throw new InvalidCommandFormatException(Command.INVALID_FORMAT_MESSAGE);
        }
    }
}
