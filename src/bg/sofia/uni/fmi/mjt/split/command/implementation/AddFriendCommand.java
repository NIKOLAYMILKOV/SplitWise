package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.FriendshipException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.split.notification.AddFriendNotification;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

public class AddFriendCommand implements Command {
    private static final String SUCCESSFUL_FRIENDSHIP_MESSAGE = "%s added in friends";
    private static final String ALREADY_FRIENDS_MESSAGE = "%s is already your friend";


    private static final int TOKENS_COUNT = 1;
    private static final int FRIEND_USERNAME_INDEX = 0;

    private final Authenticator authenticator;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final String[] tokens;

    public AddFriendCommand(Authenticator authenticator, FriendshipRepository friendshipRepository,
                            UserRepository userRepository, NotificationRepository notificationRepository,
                            String[] tokens) {
        this.authenticator = authenticator;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.tokens = tokens;
    }

    @Override
    public String execute() throws InvalidCommandFormatException {
        validate(tokens);
        String friendUsername = tokens[FRIEND_USERNAME_INDEX];

        if (!authenticator.isAuthenticated()) {
            throw new UserNotLoggedInException(LOG_IN_REQUEST_MESSAGE);
        }
        String username = authenticator.getAuthenticatedUser().username();

        if (!userRepository.isRegistered(friendUsername)) {
            throw new UserNotRegisteredException("There is no user with username " + friendUsername);
        }
        if (friendUsername.equals(username)) {
            throw new FriendshipException("You cannot friend yourself");
        }

        boolean successful = friendshipRepository.addFriendship(new Friendship(username, friendUsername));

        if (!successful) {
            throw new UserAlreadyFriendsException(String.format(ALREADY_FRIENDS_MESSAGE, friendUsername));
        }
        friendshipRepository.save();

        notificationRepository.addFriendNotification(friendUsername, AddFriendNotification.get(username));
        notificationRepository.save();

        return String.format(SUCCESSFUL_FRIENDSHIP_MESSAGE, friendUsername);
    }

    void validate(String[] tokens) throws InvalidCommandFormatException {
        if (tokens.length < TOKENS_COUNT) {
            throw new InvalidCommandFormatException(Command.INVALID_FORMAT_MESSAGE);
        }
    }
}
