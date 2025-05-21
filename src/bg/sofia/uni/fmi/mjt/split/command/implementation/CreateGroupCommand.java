package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.split.notification.AddInGroupNotification;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroupCommand implements Command {
    private static final String SUCCESSFUL_GROUP_CREATION_MESSAGE = "Group %s successfully created";
    private static final String ALREADY_EXISTING_GROUP_MESSAGE = "Group %s with that members already exists";
    private static final String INVALID_MEMBER_COUNT_MESSAGE = "There must be at least %d members in the group";
    private static final String NOT_REGISTERED_USER_MESSAGE = "There is no user with username %s";

    private static final int TOKENS_COUNT = 3;
    private static final int MIN_MEMBERS_COUNT = 3;
    private static final int GROUP_NAME_INDEX = 0;

    private final Authenticator authenticator;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final String[] tokens;

    public CreateGroupCommand(Authenticator authenticator, UserRepository userRepository,
                              GroupRepository groupRepository, NotificationRepository notificationRepository,
                              String[] tokens) {
        this.authenticator = authenticator;
        this.userRepository = userRepository;
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
        validateTokens(tokens);
        Set<String> members = Arrays.stream(tokens)
            .skip(1)
            .collect(Collectors.toSet());
        members.add(username);

        validateMembers(members);
        String groupName = tokens[GROUP_NAME_INDEX];
        boolean successful = groupRepository.addGroup(new Group(groupName, members));

        if (!successful) {
            throw new GroupAlreadyExistsException(String.format(ALREADY_EXISTING_GROUP_MESSAGE, groupName));
        }
        groupRepository.save();

        members.stream().filter(m -> !m.equals(username))
            .forEach(m -> notificationRepository.addGroupNotification(m, groupName,
                AddInGroupNotification.get(username, groupName)));
        notificationRepository.save();
        return String.format(SUCCESSFUL_GROUP_CREATION_MESSAGE, groupName);
    }

    void validateTokens(String[] tokens) throws InvalidCommandFormatException {
        if (tokens.length < TOKENS_COUNT) {
            throw new InvalidCommandFormatException(Command.INVALID_FORMAT_MESSAGE);
        }
    }

    void validateMembers(Set<String> members) throws InvalidCommandFormatException {
        if (members.size() < MIN_MEMBERS_COUNT) {
            throw new InvalidCommandFormatException(String.format(INVALID_MEMBER_COUNT_MESSAGE, MIN_MEMBERS_COUNT));
        }

        for (String member : members) {
            if (!userRepository.isRegistered(member)) {
                throw new UserNotRegisteredException(String.format(NOT_REGISTERED_USER_MESSAGE, member));
            }
        }
    }
}
