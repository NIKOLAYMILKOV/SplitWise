package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.UserNotFriendException;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PayedCommandTest {

    private static final String USERNAME = "testUser";
    private static final String FRIEND_USERNAME = "friendUser";
    private static final String AMOUNT = "50.0";

    @Mock
    private Authenticator authenticator;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private User mockUser;
    @Mock
    private Friendship mockFriendship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.username()).thenReturn(USERNAME);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);
    }

    @Test
    void testExecuteSuccess() throws InvalidCommandFormatException {
        String[] tokens = {AMOUNT, FRIEND_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(friendshipRepository.areFriends(USERNAME, FRIEND_USERNAME)).thenReturn(true);
        when(friendshipRepository.getFriendshipByUsernames(USERNAME, FRIEND_USERNAME))
            .thenReturn(Optional.of(mockFriendship));

        PayedCommand command = new PayedCommand(authenticator, friendshipRepository, notificationRepository, tokens);
        String result = command.execute();

        verify(mockFriendship).pay(FRIEND_USERNAME, Double.parseDouble(AMOUNT));
        verify(friendshipRepository).save();
        verify(notificationRepository).addFriendNotification(eq(FRIEND_USERNAME), any(String.class));
        verify(notificationRepository).save();

        assertEquals("Successful payment", result);
    }

    @Test
    void testExecuteFailsWhenNotLoggedIn() {
        String[] tokens = {AMOUNT, FRIEND_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(false);

        PayedCommand command = new PayedCommand(authenticator, friendshipRepository, notificationRepository, tokens);
        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenNotFriends() {
        String[] tokens = {AMOUNT, FRIEND_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(friendshipRepository.areFriends(USERNAME, FRIEND_USERNAME)).thenReturn(false);

        PayedCommand command = new PayedCommand(authenticator, friendshipRepository, notificationRepository, tokens);
        assertThrows(UserNotFriendException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithInvalidCommandFormat() {
        String[] tokens = {"invalidAmount", FRIEND_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(friendshipRepository.areFriends(USERNAME, FRIEND_USERNAME)).thenReturn(true);

        PayedCommand command = new PayedCommand(authenticator, friendshipRepository, notificationRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithMissingTokens() {
        String[] tokens = {AMOUNT};
        when(authenticator.isAuthenticated()).thenReturn(true);

        PayedCommand command = new PayedCommand(authenticator, friendshipRepository, notificationRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }
}
