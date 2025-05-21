package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.User;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserRepository {
    private static final String USER_REPOSITORY_FILE = "users.json";
    private static final String DATABASE_FILE_DIRECTORY = "database";
    private static final Path PATH = Paths.get(DATABASE_FILE_DIRECTORY, USER_REPOSITORY_FILE);

    private Set<User> users;
    private final Gson gson;

    public UserRepository() {
        gson = new Gson();

        try {
            if (Files.notExists(PATH)) {
                Files.createFile(PATH);
            } else {
                Type type = new TypeToken<Set<User>>() {
                }.getType();
                users = gson.fromJson(new JsonReader(new FileReader(PATH.toString())), type);
            }
        } catch (IOException ioe) {
            throw new UserRepositoryException("Could not load user database file", ioe);
        }
        if (users == null) {
            users = new HashSet<>();
        }
    }

    public Optional<User> getUserByUsername(String usernameToSearch) {
        return users.stream()
            .filter(user -> user.username().equals(usernameToSearch))
            .findFirst();
    }

    public synchronized boolean register(User user) {
        return users.add(user);
    }

    public boolean isRegistered(String username) {
        return users.stream()
            .anyMatch(u -> u.username().equals(username));
    }

    public List<User> getUsers() {
        return users.stream().toList();
    }

    public synchronized void save() {
        try (FileWriter writer = new FileWriter(PATH.toString())) {
            writer.write(gson.toJson(users));
        } catch (IOException ioe) {
            throw new UserRepositoryException("Could not save user data", ioe);
        }
    }
}
