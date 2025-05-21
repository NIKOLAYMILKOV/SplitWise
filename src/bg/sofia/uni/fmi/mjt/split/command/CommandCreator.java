package bg.sofia.uni.fmi.mjt.split.command;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.implementation.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.GetStatusCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.HelpCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.LogInCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.LogOutCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.PayedCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.PayedGroupCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.RegisterCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.SplitCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.SplitGroupCommand;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.SplitService;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {

    private static final String REGISTER = "register";
    private static final String LOG_IN = "log-in";
    private static final String LOG_OUT = "log-out";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String GET_STATUS = "get-status";
    private static final String PAYED = "payed";
    private static final String PAYED_GROUP = "payed-group";
    private static final String HELP = "help";
    private static final String SEPARATOR = " ";
    private static final String UNKNOWN_COMMAND_MESSAGE =
        "Unknown command. Type \"help\" to see the right command format";

    private final UserRepository userRepository;
    private final Authenticator authenticator;
    private final FriendshipRepository friendshipRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;

    public CommandCreator(SplitService splitService, Authenticator authenticator) {
        this.authenticator = authenticator;
        this.userRepository = splitService.getUserRepository();
        this.friendshipRepository = splitService.getFriendshipRepository();
        this.groupRepository = splitService.getGroupRepository();
        this.notificationRepository = splitService.getNotificationRepository();
    }

    public Command newCommand(String clientInput) throws InvalidCommandFormatException {
        List<String> tokens = getCommandArguments(clientInput);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return switch (tokens.getFirst()) {
            case LOG_IN -> new LogInCommand(authenticator, notificationRepository, args);
            case LOG_OUT -> new LogOutCommand(authenticator);
            case REGISTER -> new RegisterCommand(userRepository, args);
            case ADD_FRIEND ->
                new AddFriendCommand(authenticator, friendshipRepository, userRepository, notificationRepository, args);
            case CREATE_GROUP ->
                new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, args);
            case SPLIT -> new SplitCommand(authenticator, friendshipRepository, notificationRepository, args);
            case SPLIT_GROUP -> new SplitGroupCommand(authenticator, groupRepository, notificationRepository, args);
            case GET_STATUS ->
                new GetStatusCommand(authenticator, friendshipRepository, groupRepository, userRepository);
            case PAYED -> new PayedCommand(authenticator, friendshipRepository, notificationRepository, args);
            case PAYED_GROUP -> new PayedGroupCommand(authenticator, groupRepository, notificationRepository, args);
            case HELP -> new HelpCommand();
            default -> throw new InvalidCommandFormatException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    private List<String> getCommandArguments(String input) {
        return Arrays.stream(input.split(SEPARATOR)).toList();
    }

}
