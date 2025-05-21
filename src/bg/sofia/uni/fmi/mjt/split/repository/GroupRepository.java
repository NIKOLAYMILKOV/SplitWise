package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.exception.server.GroupRepositoryException;
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

public class GroupRepository {
    private static final String GROUP_REPOSITORY_FILE = "groups.json";
    private static final String DATABASE_FILE_DIRECTORY = "database";
    private static final Path PATH = Paths.get(DATABASE_FILE_DIRECTORY, GROUP_REPOSITORY_FILE);

    private Set<Group> groups;
    private final Gson gson;

    public GroupRepository() {
        gson = new Gson();

        try {
            if (Files.notExists(PATH)) {
                Files.createFile(PATH);
            } else {
                Type type = new TypeToken<Set<Group>>() {
                }.getType();
                groups = gson.fromJson(new JsonReader(new FileReader(PATH.toString())), type);
            }
        } catch (IOException ioe) {
            throw new UserRepositoryException("Could not load friendship database file", ioe);
        }
        if (groups == null) {
            groups = new HashSet<>();
        }
    }

    public Optional<Group> getGroupByNameAndMember(String groupNameToSearch, String memberUsername) {
        return groups.stream()
            .filter(group -> group.name().equals(groupNameToSearch) && group.isMember(memberUsername))
            .findFirst();
    }

    public List<Group> getGroupsByMember(String memberUsername) {
        return groups.stream()
            .filter(group ->group.isMember(memberUsername))
            .toList();
    }

    public synchronized boolean addGroup(Group group) {
        return groups.add(group);
    }

    public Set<Group> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public synchronized void save() {
        try (FileWriter writer = new FileWriter(PATH.toString())) {
            writer.write(gson.toJson(groups));
        } catch (IOException ioe) {
            throw new GroupRepositoryException("Could not save group data", ioe);
        }
    }
}
