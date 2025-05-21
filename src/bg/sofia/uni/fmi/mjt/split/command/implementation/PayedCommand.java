package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.UserNotFriendException;
import bg.sofia.uni.fmi.mjt.split.notification.PaymentApproveNotification;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;

public class PayedCommand implements Command {

    private static final String SUCCESSFUL_PAYMENT_MESSAGE = "Successful payment";
    private static final String USER_NOT_FRIEND_MESSAGE = "%s is not in your friend list";
    private static final int MIN_TOKEN_COUNT = 2;
    private static final int AMOUNT_INDEX = 0;
    private static final int USERNAME_INDEX = 1;

    private final Authenticator authenticator;
    private final FriendshipRepository friendshipRepository;
    NotificationRepository notificationRepository;
    private final String[] tokens;

    public PayedCommand(Authenticator authenticator, FriendshipRepository friendshipRepository,
                        NotificationRepository notificationRepository, String[] tokens) {
        this.authenticator = authenticator;
        this.friendshipRepository = friendshipRepository;
        this.notificationRepository = notificationRepository;
        this.tokens = tokens;
    }

    @Override
    public String execute() throws InvalidCommandFormatException {
        if (!authenticator.isAuthenticated()) {
            throw new UserNotLoggedInException(LOG_IN_REQUEST_MESSAGE);
        }
        String username = authenticator.getAuthenticatedUser().username();
        validateTokens();
        validateUser();

        friendshipRepository
            .getFriendshipByUsernames(username, tokens[USERNAME_INDEX])
            .get()
            .pay(tokens[USERNAME_INDEX], Double.parseDouble(tokens[AMOUNT_INDEX]));
        friendshipRepository.save();

        notificationRepository.addFriendNotification(tokens[USERNAME_INDEX],
            PaymentApproveNotification.get(username, Double.parseDouble(tokens[AMOUNT_INDEX])));
        notificationRepository.save();
        return String.format(SUCCESSFUL_PAYMENT_MESSAGE);
    }

    void validateTokens() throws InvalidCommandFormatException {
        if (tokens.length < MIN_TOKEN_COUNT) {
            throw new InvalidCommandFormatException(Command.INVALID_FORMAT_MESSAGE);
        }
        try {
            Double.parseDouble(tokens[AMOUNT_INDEX]);
        } catch (NumberFormatException nfe) {
            throw new InvalidCommandFormatException(INVALID_AMOUNT_MESSAGE, nfe);
        }
    }

    void validateUser() {
        if (!friendshipRepository.areFriends(authenticator.getAuthenticatedUser().username(), tokens[USERNAME_INDEX])) {
            throw new UserNotFriendException(String.format(USER_NOT_FRIEND_MESSAGE, tokens[USERNAME_INDEX]));
        }
    }
}
