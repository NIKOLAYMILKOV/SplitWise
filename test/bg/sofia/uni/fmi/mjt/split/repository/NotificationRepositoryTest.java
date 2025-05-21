package bg.sofia.uni.fmi.mjt.split.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class NotificationRepositoryTest {
    private static final String USERNAME = "testUser";
    private static final String GROUP_NAME = "testGroup";
    private static final String FRIEND_NOTIFICATION = "Friend split notification";
    private static final String GROUP_NOTIFICATION = "Group split notification";

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        notificationRepository = new NotificationRepository();
    }

    @Test
    void testAddFriendNotification() {
        notificationRepository.addFriendNotification(USERNAME, FRIEND_NOTIFICATION);
        String notifications = notificationRepository.notify(USERNAME);

        assertTrue(notifications.contains(FRIEND_NOTIFICATION));
    }

    @Test
    void testAddGroupNotification() {
        notificationRepository.addGroupNotification(USERNAME, GROUP_NAME, GROUP_NOTIFICATION);
        String notifications = notificationRepository.notify(USERNAME);

        assertTrue(notifications.contains(GROUP_NOTIFICATION));
    }

    @Test
    void testNotifyClearsNotifications() {
        notificationRepository.addFriendNotification(USERNAME, FRIEND_NOTIFICATION);
        notificationRepository.notify(USERNAME);
        String notificationsAfterClear = notificationRepository.notify(USERNAME);

        assertEquals("No notifications to show.", notificationsAfterClear);
    }
}

