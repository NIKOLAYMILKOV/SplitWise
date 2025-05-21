package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.UserNotGroupMemberException;
import bg.sofia.uni.fmi.mjt.split.notification.PaymentApproveNotification;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupException;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;

import java.util.Optional;

public class PayedGroupCommand implements Command {

    private static final String SUCCESSFUL_GROUP_PAY_MESSAGE = "Successful payment";
    private static final String NO_SUCH_GROUP_MESSAGE = "There is no group with name %s that you participate in";
    private static final String USER_NOT_MEMBER_MESSAGE = "There is not member of this group with username %s";
    private static final int AMOUNT_INDEX = 0;
    private static final int GROUP_NAME_INDEX = 1;
    private static final int PAYER_USERNAME_INDEX = 2;
    private static final int MIN_TOKEN_COUNT = 3;

    private final Authenticator authenticator;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final String[] tokens;

    public PayedGroupCommand(Authenticator authenticator, GroupRepository groupRepository,
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
            throw new GroupException(String.format(NO_SUCH_GROUP_MESSAGE, tokens[GROUP_NAME_INDEX]));
        }
        Group group = optionalGroup.get();
        validateUser(tokens[PAYER_USERNAME_INDEX], group);

        group.pay(username, tokens[PAYER_USERNAME_INDEX], Double.parseDouble(tokens[AMOUNT_INDEX]));
        groupRepository.save();

        notificationRepository.addGroupNotification(tokens[PAYER_USERNAME_INDEX], tokens[GROUP_NAME_INDEX],
            PaymentApproveNotification.get(username, Double.parseDouble(tokens[AMOUNT_INDEX])));
        notificationRepository.save();
        return String.format(SUCCESSFUL_GROUP_PAY_MESSAGE);
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

    void validateUser(String username, Group group) {
        if (!group.isMember(username)) {
            throw new UserNotGroupMemberException(String.format(USER_NOT_MEMBER_MESSAGE, username));
        }
    }

}
