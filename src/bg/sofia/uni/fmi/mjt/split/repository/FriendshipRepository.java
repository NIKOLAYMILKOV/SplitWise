package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import bg.sofia.uni.fmi.mjt.split.exception.server.FriendshipRepositoryException;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FriendshipRepository {
    private static final String FRIENDSHIP_REPOSITORY_FILE = "friendships.json";
    private static final String DATABASE_FILE_DIRECTORY = "database";
    private static final Path PATH = Paths.get(DATABASE_FILE_DIRECTORY, FRIENDSHIP_REPOSITORY_FILE);

    private Set<Friendship> friendships;
    private final Gson gson;

    public FriendshipRepository() {
        gson = new Gson();

        try {
            if (Files.notExists(PATH)) {
                Files.createFile(PATH);
            } else {
                Type type = new TypeToken<Set<Friendship>>() {
                }.getType();
                friendships = gson.fromJson(new JsonReader(new FileReader(PATH.toString())), type);
            }
        } catch (IOException ioe) {
            throw new UserRepositoryException("Could not load friendship database file", ioe);
        }
        if (friendships == null) {
            friendships = new HashSet<>();
        }
    }

    public synchronized Optional<Friendship> getFriendshipByUsernames(String firstUsername, String secondUsername) {
        return friendships.stream()
            .filter(f -> f.isMember(firstUsername) && f.isMember(secondUsername))
            .findFirst();
    }

    public boolean areFriends(String firstUsername, String secondUsername) {
        return friendships.stream().anyMatch(f -> f.isMember(firstUsername) && f.isMember(secondUsername));
    }

    public synchronized boolean addFriendship(Friendship friendship) {
        return friendships.add(friendship);
    }

    public Set<Friendship> getFriendships() {
        return Collections.unmodifiableSet(friendships);
    }

    public List<Friendship> getFriendshipsByUsername(String username) {
        return friendships.stream()
            .filter(f -> f.isMember(username))
            .toList();
    }

    public synchronized void save() {
        try (FileWriter writer = new FileWriter(PATH.toString())) {
            writer.write(gson.toJson(friendships));
        } catch (IOException ioe) {
            throw new FriendshipRepositoryException("Could not save friendship data", ioe);
        }
    }
}
