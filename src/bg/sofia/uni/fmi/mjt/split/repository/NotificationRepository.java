package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.NotificationFeed;
import bg.sofia.uni.fmi.mjt.split.exception.server.NotificationRepositoryException;
import bg.sofia.uni.fmi.mjt.split.exception.server.UserRepositoryException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NotificationRepository {

    private static final String NOTIFICATION_REPOSITORY_FILE = "notifications.json";
    private static final String DATABASE_FILE_DIRECTORY = "database";
    private static final Path PATH = Paths.get(DATABASE_FILE_DIRECTORY, NOTIFICATION_REPOSITORY_FILE);
    private static final String NO_NOTIFICATIONS_MESSAGE = "No notifications to show.";

    private ConcurrentMap<String, NotificationFeed> notificationFeedMap;
    private final Gson gson;

    public NotificationRepository() {
        gson = new Gson();
//        Path path = Path.of(DATABASE_FILE_DIRECTORY, NOTIFICATION_REPOSITORY_FILE);
//
//        try {
//            Type type = new TypeToken<ConcurrentHashMap<String, NotificationFeed>>() {
//            }.getType();
//            notificationFeedMap = gson.fromJson(new JsonReader(new FileReader(path.toString())), type);
//        } catch (IOException ioe) {
//            throw new NotificationRepositoryException("Could not load notification database file", ioe);
//        }
//        if (notificationFeedMap == null) {
//            notificationFeedMap = new ConcurrentHashMap<>();
//        }

        try {
            if (Files.notExists(PATH)) {
                Files.createFile(PATH);
            } else {
                Type type = new TypeToken<ConcurrentHashMap<String, NotificationFeed>>() {
                }.getType();
                notificationFeedMap = gson.fromJson(new JsonReader(new FileReader(PATH.toString())), type);
            }
        } catch (IOException ioe) {
            throw new UserRepositoryException("Could not load notification database file", ioe);
        }
        if (notificationFeedMap == null) {
            notificationFeedMap = new ConcurrentHashMap<>();
        }
    }

    public void addFriendNotification(String username, String notification) {
        notificationFeedMap.putIfAbsent(username, new NotificationFeed());
        notificationFeedMap.get(username).addFriendNotification(notification);
    }

    public void addGroupNotification(String username, String groupName, String notification) {
        notificationFeedMap.putIfAbsent(username, new NotificationFeed());
        notificationFeedMap.get(username).addGroupNotification(groupName, notification);
    }

    public String notify(String username) {
        NotificationFeed notificationFeed = notificationFeedMap.get(username);
        if (notificationFeed == null || notificationFeed.isEmpty()) {
            return NO_NOTIFICATIONS_MESSAGE;
        }
        String result = notificationFeed.toString();
        notificationFeed.clear();
        return result;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(PATH.toString())) {
            writer.write(gson.toJson(notificationFeedMap));
        } catch (IOException ioe) {
            throw new NotificationRepositoryException("Could not save notification data", ioe);
        }
    }

}
