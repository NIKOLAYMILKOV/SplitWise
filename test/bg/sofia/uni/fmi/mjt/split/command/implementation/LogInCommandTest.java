package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.AlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.InvalidUsernameOrPasswordException;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogInCommandTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPass";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Mock
    private Authenticator authenticatorMock;
    @Mock
    private NotificationRepository notificationRepositoryMock;
    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.username()).thenReturn(USERNAME);
        when(mockUser.firstName()).thenReturn(FIRST_NAME);
        when(mockUser.lastName()).thenReturn(LAST_NAME);
    }

    @Test
    void testExecuteSuccess() throws InvalidCommandFormatException {
        String[] tokens = {USERNAME, PASSWORD};
        when(authenticatorMock.isAuthenticated()).thenReturn(false);
        when(authenticatorMock.login(USERNAME, PASSWORD)).thenReturn(true);
        when(authenticatorMock.getAuthenticatedUser()).thenReturn(mockUser);
        when(notificationRepositoryMock.notify(USERNAME)).thenReturn("No new notifications.");

        LogInCommand command = new LogInCommand(authenticatorMock, notificationRepositoryMock, tokens);
        String result = command.execute();

        assertEquals("Welcome John Doe \n No new notifications.", result);
    }

    @Test
    void testExecuteFailsWhenAlreadyLoggedIn() {
        String[] tokens = {USERNAME, PASSWORD};
        when(authenticatorMock.isAuthenticated()).thenReturn(true);

        LogInCommand command = new LogInCommand(authenticatorMock, notificationRepositoryMock, tokens);
        assertThrows(AlreadyLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithInvalidCredentials() {
        String[] tokens = {USERNAME, PASSWORD};
        when(authenticatorMock.isAuthenticated()).thenReturn(false);
        when(authenticatorMock.login(USERNAME, PASSWORD)).thenReturn(false);

        LogInCommand command = new LogInCommand(authenticatorMock, notificationRepositoryMock, tokens);
        assertThrows(InvalidUsernameOrPasswordException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithInvalidCommandFormat() {
        String[] tokens = {USERNAME};
        when(authenticatorMock.isAuthenticated()).thenReturn(false);

        LogInCommand command = new LogInCommand(authenticatorMock, notificationRepositoryMock, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }
}
