package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterCommandTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
    }

    @Test
    void testRegisterUserSuccessfully() {
        User user = new User("testUser", "password123", "FirstName", "LastName");
        when(userRepository.register(user)).thenReturn(true);
        when(userRepository.isRegistered("testUser")).thenReturn(true);

        assertTrue(userRepository.register(user), "User should be registered successfully");
        assertTrue(userRepository.isRegistered("testUser"), "User should be found in the repository");
    }

    @Test
    void testRegisterDuplicateUser() {
        User user = new User("testUser", "password123", "FirstName", "LastName");
        when(userRepository.register(user)).thenReturn(false);

        assertFalse(userRepository.register(user), "Duplicate username should not be allowed");
    }

    @Test
    void testGetUserByUsernameWhenUserExists() {
        User user = new User("existingUser", "pass", "FirstName", "LastName");
        when(userRepository.getUserByUsername("existingUser")).thenReturn(Optional.of(user));

        Optional<User> retrievedUser = userRepository.getUserByUsername("existingUser");
        assertTrue(retrievedUser.isPresent(), "User should be found");
        assertEquals(user, retrievedUser.get(), "Retrieved user should match the stored one");
    }

    @Test
    void testGetUserByUsernameWhenUserDoesNotExist() {
        when(userRepository.getUserByUsername("nonExistentUser")).thenReturn(Optional.empty());
        Optional<User> user = userRepository.getUserByUsername("nonExistentUser");
        assertFalse(user.isPresent(), "User should not be found");
    }

    @Test
    void testRegisterCommandSuccessfully() throws InvalidCommandFormatException {
        String[] tokens = {"testUser", "password123", "First", "Last"};
        RegisterCommand registerCommand = new RegisterCommand(userRepository, tokens);
        when(userRepository.register(any(User.class))).thenReturn(true);
        when(userRepository.isRegistered("testUser")).thenReturn(true);

        String result = registerCommand.execute();
        assertEquals("First Last registered successfully", result, "Registration message should match");
    }

    @Test
    void testRegisterCommandWithExistingUser() throws InvalidCommandFormatException {
        String[] tokens = {"testUser", "password123", "First", "Last"};
        RegisterCommand registerCommand = new RegisterCommand(userRepository, tokens);
        when(userRepository.register(any(User.class))).thenReturn(false);

//        String result = registerCommand.execute();
        assertThrows(UserAlreadyRegisteredException.class, registerCommand::execute);
//        assertEquals("First Last is already registered", result, "Duplicate registration message should match");
    }

    @Test
    void testRegisterCommandWithInvalidArguments() {
        String[] tokens = {"testUser", "password123"}; // Missing first and last name
        RegisterCommand registerCommand = new RegisterCommand(userRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, registerCommand::execute, "Should throw exception for invalid command format");
    }
}
