package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.UserNotFriendException;
import bg.sofia.uni.fmi.mjt.split.notification.SplitRequestNotification;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SplitCommandTest {
    private static final String AUTHENTICATED_USERNAME = "testUser";
    private static final String FRIEND_USERNAME = "friendUser";
    private static final double AMOUNT = 10.0;
    private static final String PAYMENT_REASON = "Dinner";

    private Authenticator authenticator;
    private FriendshipRepository friendshipRepository;
    private NotificationRepository notificationRepository;
    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticator = mock(Authenticator.class);
        friendshipRepository = mock(FriendshipRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        authenticatedUser = mock(User.class);
    }

    @Test
    void testExecuteSuccessfulSplit() throws InvalidCommandFormatException {
        String[] tokens = {"10", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.username()).thenReturn(AUTHENTICATED_USERNAME);
        when(friendshipRepository.areFriends(AUTHENTICATED_USERNAME, FRIEND_USERNAME)).thenReturn(true);
        var friendship = mock(Friendship.class);
        when(friendshipRepository.getFriendshipByUsernames(AUTHENTICATED_USERNAME, FRIEND_USERNAME))
            .thenReturn(Optional.of(friendship));

        SplitCommand command = new SplitCommand(authenticator, friendshipRepository, notificationRepository, tokens);

        String result = command.execute();

        verify(friendship).split(AUTHENTICATED_USERNAME, AMOUNT);
        verify(friendshipRepository).save();
        verify(notificationRepository).addFriendNotification(FRIEND_USERNAME,
            SplitRequestNotification.get(AUTHENTICATED_USERNAME, AMOUNT / 2, PAYMENT_REASON));
        verify(notificationRepository).save();
        assertEquals("Split request is successfully sent", result);
    }

    @Test
    void testExecuteThrowsUserNotLoggedInException() {
        String[] tokens = {"10", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticator.isAuthenticated()).thenReturn(false);

        SplitCommand command = new SplitCommand(authenticator, friendshipRepository, notificationRepository, tokens);

        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteThrowsUserNotFriendException() {
        String[] tokens = {"10", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.username()).thenReturn(AUTHENTICATED_USERNAME);
        when(friendshipRepository.areFriends(AUTHENTICATED_USERNAME, FRIEND_USERNAME)).thenReturn(false);

        SplitCommand command = new SplitCommand(authenticator, friendshipRepository, notificationRepository, tokens);

        assertThrows(UserNotFriendException.class, command::execute);
    }

    @Test
    void testExecuteThrowsInvalidCommandFormatExceptionForInvalidAmount() {
        String[] tokens = {"invalidAmount", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.username()).thenReturn(AUTHENTICATED_USERNAME);

        SplitCommand command = new SplitCommand(authenticator, friendshipRepository, notificationRepository, tokens);

        assertThrows(InvalidCommandFormatException.class, command::execute);
    }

    @Test
    void testExecuteThrowsInvalidCommandFormatExceptionForMissingArguments() {
        String[] tokens = {"10", FRIEND_USERNAME}; // Missing reason
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.username()).thenReturn(AUTHENTICATED_USERNAME);

        SplitCommand command = new SplitCommand(authenticator, friendshipRepository, notificationRepository, tokens);

        assertThrows(InvalidCommandFormatException.class, command::execute);
    }
}
