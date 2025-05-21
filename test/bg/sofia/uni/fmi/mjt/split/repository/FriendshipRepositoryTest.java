package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FriendshipRepositoryTest {
    private FriendshipRepository friendshipRepository;

    @BeforeEach
    void setUp() {
        // Create a fresh instance before each test
        friendshipRepository = new FriendshipRepository();
    }

    @Test
    void testAddFriendship() {
        Friendship friendship = new Friendship("user1", "user2");
        assertTrue(friendshipRepository.addFriendship(friendship));
        assertTrue(friendshipRepository.areFriends("user1", "user2"));
    }

    @Test
    void testGetFriendshipByUsernames() {
        Friendship friendship = new Friendship("user1", "user2");
        friendshipRepository.addFriendship(friendship);

        Optional<Friendship> retrievedFriendship = friendshipRepository.getFriendshipByUsernames("user1", "user2");
        assertTrue(retrievedFriendship.isPresent());
        assertEquals(friendship, retrievedFriendship.get());
    }

    @Test
    void testAreFriends() {
        friendshipRepository.addFriendship(new Friendship("user1", "user2"));
        assertTrue(friendshipRepository.areFriends("user1", "user2"));
        assertFalse(friendshipRepository.areFriends("user1", "user3"));
    }

}
