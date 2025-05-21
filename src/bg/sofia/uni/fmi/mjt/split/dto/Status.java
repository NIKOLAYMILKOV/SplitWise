package bg.sofia.uni.fmi.mjt.split.dto;

import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

import java.util.List;

public class Status {
    private static final String BORROWER_MESSAGE = "* %s %s (%s): You owe %.2f LV\n";
    private static final String LENDER_MESSAGE = "* %s %s (%s): Owes you %.2f LV\n";
    private static final String EMPTY_STATUS_MESSAGE = "You have no unsettled debt";


    private final String usernameToRequestStatus;
    private final List<Friendship> friendships;
    private final List<Group> groups;
    private final UserRepository userRepository;

    public Status(String username, FriendshipRepository friendshipRepository, GroupRepository groupRepository,
                  UserRepository userRepository) {
        friendships = friendshipRepository.getFriendshipsByUsername(username);
        groups = groupRepository.getGroupsByMember(username);
        this.userRepository = userRepository;
        this.usernameToRequestStatus = username;
    }

    @Override
    public String toString() {
        String result = getFriendshipStatus() + getGroupStatus();
        return result.isBlank() ? EMPTY_STATUS_MESSAGE : result;
    }

    private String getFriendshipStatus() {
        StringBuilder result = new StringBuilder();
        boolean hasStatusToShow = false;

        for (Friendship friendship : friendships) {
            if (friendship.hasUnsettledDebt()) {
                hasStatusToShow = true;

                String otherUser = friendship.borrower().equals(usernameToRequestStatus)
                    ? friendship.lender()
                    : friendship.borrower();

                User user = userRepository.getUserByUsername(otherUser)
                    .orElseThrow(() -> new RuntimeException("User not found"));

                String message = friendship.borrower().equals(usernameToRequestStatus)
                    ? String.format(BORROWER_MESSAGE, user.firstName(), user.lastName(), user.username(),
                    friendship.amount())
                    : String.format(LENDER_MESSAGE, user.firstName(), user.lastName(), user.username(),
                    friendship.amount());

                result.append(message);
            }
        }

        return hasStatusToShow ? "Friends:\n" + result : "";
    }

    private String getGroupStatus() {
        StringBuilder result = new StringBuilder("Groups:\n");
        boolean hasStatusToShow = false;
        for (Group group : groups) {
            StringBuilder currentGroup = new StringBuilder("-").append(group.name()).append("\n");
            boolean hasStatusInCurrentGroup = false;

            for (Split split : group.splits()) {
                if (split.hasUnsettledDebt() && split.isMember(usernameToRequestStatus)) {
                    hasStatusToShow = true;
                    hasStatusInCurrentGroup = true;

                    User user = userRepository.getUserByUsername(
                        split.borrower().equals(usernameToRequestStatus) ? split.lender() : split.borrower()).get();

                    String message = split.borrower().equals(usernameToRequestStatus)
                        ? String.format(BORROWER_MESSAGE, user.firstName(), user.lastName(), user.username(),
                        split.amount())
                        : String.format(LENDER_MESSAGE, user.firstName(), user.lastName(), user.username(),
                        split.amount());

                    currentGroup.append(message);
                }
            }
            if (hasStatusInCurrentGroup) {
                result.append(currentGroup);
            }
        }
        return hasStatusToShow ? result.substring(0, result.length() - 1) : "";
    }

}
