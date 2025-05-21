package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.Command;
import bg.sofia.uni.fmi.mjt.split.dto.Status;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

public class GetStatusCommand implements Command {

    private final Authenticator authenticator;
    private final FriendshipRepository friendshipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GetStatusCommand(Authenticator authenticator, FriendshipRepository friendshipRepository,
                            GroupRepository groupRepository, UserRepository userRepository) {
        this.authenticator = authenticator;
        this.friendshipRepository = friendshipRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String execute() {
        if (!authenticator.isAuthenticated()) {
            throw new UserNotLoggedInException(LOG_IN_REQUEST_MESSAGE);
        }
        Status status =
            new Status(authenticator.getAuthenticatedUser().username(), friendshipRepository, groupRepository,
                userRepository);
        return status.toString();
    }
}
