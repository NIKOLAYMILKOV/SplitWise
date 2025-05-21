package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.NoSuchGroupException;
import bg.sofia.uni.fmi.mjt.split.notification.SplitRequestNotification;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;

import java.util.Optional;

public class SplitGroupCommand implements Command {

    private static final String SUCCESSFUL_GROUP_SPLIT_MESSAGE = "Split request is successfully sent";
    private static final String NO_SUCH_GROUP_MESSAGE = "There is no group with name %s that you participate in";
    private static final int AMOUNT_INDEX = 0;
    private static final int GROUP_NAME_INDEX = 1;
    private static final int PAYMENT_REASON_INDEX = 2;
    private static final int MIN_TOKEN_COUNT = 3;

    private final Authenticator authenticator;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final String[] tokens;

    public SplitGroupCommand(Authenticator authenticator, GroupRepository groupRepository,
                             NotificationRepository notificationRepository, String[] tokens) {
        this.authenticator = authenticator;
        this.groupRepository = groupRepository;
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
        Optional<Group> optionalGroup = groupRepository
            .getGroupByNameAndMember(tokens[GROUP_NAME_INDEX], username);
        if (optionalGroup.isEmpty()) {
            throw new NoSuchGroupException(String.format(NO_SUCH_GROUP_MESSAGE, tokens[GROUP_NAME_INDEX]));
        }
        Group group = optionalGroup.get();
        String groupName = group.name();

        group.split(username, Double.parseDouble(tokens[AMOUNT_INDEX]));
        groupRepository.save();

        group.members().stream()
            .filter(m -> !m.equals(username))
            .forEach(m -> notificationRepository
                .addGroupNotification(m, groupName, SplitRequestNotification
                    .get(username, Double.parseDouble(tokens[AMOUNT_INDEX]) / group.members().size()
                        , tokens[PAYMENT_REASON_INDEX])));

        notificationRepository.save();
        return String.format(SUCCESSFUL_GROUP_SPLIT_MESSAGE);
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

}
