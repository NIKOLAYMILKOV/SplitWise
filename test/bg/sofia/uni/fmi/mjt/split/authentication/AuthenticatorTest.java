package bg.sofia.uni.fmi.mjt.split.authentication;

import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.encryption.PasswordHashUtil;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticatorTest {
    private static final String username = "niki";
    private static final String password = "1234";

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private Authenticator authenticator;

    @Test
    void TestLogInWithRegisteredUser() {
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(
            Optional.of(new User(username, PasswordHashUtil.hashPassword(password), "FirstName", "LastName")));
        assertTrue(authenticator.login(username, password), "Should return true when user is registered");
    }

    @Test
    void testLogInWithUnregisteredUser() {
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(Optional.empty());
        assertFalse(authenticator.login(username, password));
    }

    @Test
    void testLogInWithRegisteredUserAndWrongPassword() {
        String wrongPassword = "0000";
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(
            Optional.of(new User(username, PasswordHashUtil.hashPassword(password), "FirstName", "LastName")));
        assertFalse(authenticator.login(username, wrongPassword));
    }

    @Test
    void testGetAuthenticatedUserBeforeLoggingIn() {
        assertNull(authenticator.getAuthenticatedUser(), "getAuthenticatedUser should return null if not logged in");
    }

    @Test
    void testGetAuthenticatedUserAfterLoggingIn() {
        User user = new User(username, PasswordHashUtil.hashPassword(password), "FirstName", "LastName");

        when(userRepositoryMock.getUserByUsername(username)).thenReturn(
            Optional.of(user));
        authenticator.login(username, password);
        assertEquals(user, authenticator.getAuthenticatedUser());
    }

    @Test
    void testIsAuthenticated() {
        assertFalse(authenticator.isAuthenticated(), "Should return false before logging in");
        logIn();
        assertTrue(authenticator.isAuthenticated(), "Should return true after logging in");
    }

    @Test
    void testLogOut() {
        logIn();
        authenticator.logout();
        assertFalse(authenticator.isAuthenticated());
    }

    private void logIn() {
        User user = new User(username, PasswordHashUtil.hashPassword(password), "FirstName", "LastName");
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(
            Optional.of(user));
        authenticator.login(username, password);
    }
}
