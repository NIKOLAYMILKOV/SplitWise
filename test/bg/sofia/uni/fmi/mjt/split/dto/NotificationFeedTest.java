package bg.sofia.uni.fmi.mjt.split.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationFeedTest {

    private NotificationFeed notificationFeed;

    @BeforeEach
    void setUp() {
        notificationFeed = new NotificationFeed();
    }

    @Test
    void testAddFriendNotification() {
        notificationFeed.addFriendNotification("New friend notification");
        assertFalse(notificationFeed.isEmpty(), "Notification feed should not be empty after adding a friend notification");
    }

    @Test
    void testAddGroupNotification() {
        notificationFeed.addGroupNotification("Group", "New group notification");
        assertFalse(notificationFeed.isEmpty(), "Notification feed should not be empty after adding a group notification");
    }

    @Test
    void testClearNotifications() {
        notificationFeed.addFriendNotification("New friend notification");
        notificationFeed.addGroupNotification("Group", "New group notification");
        notificationFeed.clear();
        assertTrue(notificationFeed.isEmpty(), "Notification feed should be empty after clearing all notifications");
    }

    @Test
    void testToStringOutput() {
        notificationFeed.addFriendNotification("New friend notification");
        notificationFeed.addGroupNotification("Group", "New group notification");

        String output = notificationFeed.toString();
        assertTrue(output.contains("New friend notification"), "Output should contain the friend notification");
        assertTrue(output.contains("Group"), "Output should contain the group name");
        assertTrue(output.contains("New group notification"), "Output should contain the group notification");
    }
}
