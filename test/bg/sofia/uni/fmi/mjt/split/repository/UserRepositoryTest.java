package bg.sofia.uni.fmi.mjt.split.repository;

import bg.sofia.uni.fmi.mjt.split.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest {
    private static int id;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void testRegisterUserSuccessfully() {
        User user = new User("testUser" + id, "password123", "firstName", "LastName");
        assertTrue(userRepository.register(user), "User should be registered successfully");
        assertTrue(userRepository.isRegistered("testUser" + id++), "User should be found in the repository");
    }

    @Test
    void testRegisterDuplicateUser() {
        User user1 = new User("testUser" + id, "password123", "firstName", "LastName");
        User user2 = new User("testUser" + id++, "password456", "FirstName", "LastName");

        assertTrue(userRepository.register(user1), "First registration should succeed");
        assertFalse(userRepository.register(user2), "Duplicate username should not be allowed");
    }

    @Test
    void testGetUserByUsernameWhenUserExists() {
        User user = new User("existingUser", "pass", "FirstName", "LastName");
        userRepository.register(user);
        Optional<User> retrievedUser = userRepository.getUserByUsername("existingUser");
        assertTrue(retrievedUser.isPresent(), "User should be found");
        assertEquals(user, retrievedUser.get(), "Retrieved user should match the stored one");
    }

    @Test
    void testGetUserByUsernameWhenUserDoesNotExist() {
        Optional<User> user = userRepository.getUserByUsername("nonExistentUser");
        assertFalse(user.isPresent(), "User should not be found");
    }
}

