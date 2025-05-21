package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GroupRepositoryTest {
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {
        groupRepository = new GroupRepository();
    }

    @Test
    void testAddGroup() {
        Group group = new Group("TestGroup", Set.of("user1", "user2", "user3"));
        assertTrue(groupRepository.addGroup(group));
        assertTrue(groupRepository.getGroupByNameAndMember("TestGroup", "user1").isPresent());
    }

    @Test
    void testGetGroupByNameAndMember() {
        Group group = new Group("TestGroup", Set.of("user1", "user2", "user3"));
        groupRepository.addGroup(group);

        Optional<Group> retrievedGroup = groupRepository.getGroupByNameAndMember("TestGroup", "user1");
        assertTrue(retrievedGroup.isPresent());
        assertEquals(group, retrievedGroup.get());
    }

    @Test
    void testGetGroupsByMember() {
        Group group1 = new Group("Group1", Set.of("user1", "user2", "user3"));
        Group group2 = new Group("Group2", Set.of("user1", "user3", "user4"));
        groupRepository.addGroup(group1);
        groupRepository.addGroup(group2);

        List<Group> userGroups = groupRepository.getGroupsByMember("user1");
        assertEquals(2, userGroups.size());
        assertTrue(userGroups.contains(group1));
        assertTrue(userGroups.contains(group2));
    }
}
