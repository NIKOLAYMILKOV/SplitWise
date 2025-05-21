package bg.sofia.uni.fmi.mjt.split.repository;

public class SplitService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final FriendshipRepository friendshipRepository;

    public static SplitServiceBuilder builder() {
        return new SplitServiceBuilder();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public NotificationRepository getNotificationRepository() {
        return notificationRepository;
    }

    public FriendshipRepository getFriendshipRepository() {
        return friendshipRepository;
    }

    public void save() {
        if (userRepository != null) {
            userRepository.save();
        }
        if (friendshipRepository != null) {
            friendshipRepository.save();
        }
        if (groupRepository != null) {
            groupRepository.save();
        }
        if (notificationRepository != null) {
            notificationRepository.save();
        }
    }

    private SplitService(SplitServiceBuilder splitServiceBuilder) {
        this.userRepository = splitServiceBuilder.userRepository;
        this.groupRepository = splitServiceBuilder.groupRepository;
        this.notificationRepository = splitServiceBuilder.notificationRepository;
        this.friendshipRepository = splitServiceBuilder.friendshipRepository;
    }

    public static class SplitServiceBuilder {
        private UserRepository userRepository;
        private GroupRepository groupRepository;
        private NotificationRepository notificationRepository;
        private FriendshipRepository friendshipRepository;

        private SplitServiceBuilder() { }

        public SplitServiceBuilder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public SplitServiceBuilder friendshipRepository(FriendshipRepository friendshipRepository) {
            this.friendshipRepository = friendshipRepository;
            return this;
        }

        public SplitServiceBuilder groupRepository(GroupRepository groupRepository) {
            this.groupRepository = groupRepository;
            return this;
        }

        public SplitServiceBuilder notificationRepository(NotificationRepository notificationRepository) {
            this.notificationRepository = notificationRepository;
            return this;
        }

        public SplitService build() {
            return new SplitService(this);
        }
    }
}
