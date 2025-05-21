package bg.sofia.uni.fmi.mjt.split.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFeed {
    private final List<String> friendNotifications;
    private final Map<String, List<String>> groupNotifications;

    public NotificationFeed() {
        friendNotifications = new ArrayList<>();
        groupNotifications = new HashMap<>();
    }

    public synchronized void addFriendNotification(String notification) {
        friendNotifications.add(notification);
    }

    public synchronized void addGroupNotification(String groupName, String notification) {
        if (!groupNotifications.containsKey(groupName)) {
            groupNotifications.put(groupName, new ArrayList<>());
        }
        groupNotifications.get(groupName).add(notification);
    }

    public void clear() {
        groupNotifications.clear();
        friendNotifications.clear();
    }

    public boolean isEmpty() {
        return groupNotifications.isEmpty() && friendNotifications.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Notifications\n");
        result.append("Friends:\n");
        for (String friendNotification : friendNotifications) {
            result.append(friendNotification).append("\n");
        }
        for (Map.Entry<String, List<String>> stringListEntry : groupNotifications.entrySet()) {
            result.append(stringListEntry.getKey()).append("\n");
            for (String notification : stringListEntry.getValue()) {
                result.append("*").append(notification).append("\n");
            }
        }
        return result.toString();
    }
}
