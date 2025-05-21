package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.friend.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.split.notification.AddFriendNotification;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddFriendCommandTest {
    private Authenticator authenticator;
    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;
    private NotificationRepository notificationRepository;
    private String[] tokens;
    private User user;

    @BeforeEach
    void setUp() {
        authenticator = mock(Authenticator.class);
        friendshipRepository = mock(FriendshipRepository.class);
        userRepository = mock(UserRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        user = mock(User.class);

        when(user.username()).thenReturn("user");
    }

    @Test
    void testExecuteNotLoggedIn() {
        tokens = new String[]{"friend"};
        when(authenticator.isAuthenticated()).thenReturn(false);

        AddFriendCommand command = new AddFriendCommand(authenticator, friendshipRepository, userRepository, notificationRepository, tokens);
        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteUserNotRegistered() {
        tokens = new String[]{"friend"};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.isRegistered("friend")).thenReturn(false);

        AddFriendCommand command = new AddFriendCommand(authenticator, friendshipRepository, userRepository, notificationRepository, tokens);
        assertThrows(UserNotRegisteredException.class, command::execute);
    }

    @Test
    void testExecuteAlreadyFriends() {
        tokens = new String[]{"friend"};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.isRegistered("friend")).thenReturn(true);
        when(friendshipRepository.addFriendship(any(Friendship.class))).thenReturn(false);

        AddFriendCommand command = new AddFriendCommand(authenticator, friendshipRepository, userRepository, notificationRepository, tokens);
        assertThrows(UserAlreadyFriendsException.class, command::execute);
    }

    @Test
    void testExecuteSuccessfulFriendship() throws InvalidCommandFormatException {
        tokens = new String[]{"friend"};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.isRegistered("friend")).thenReturn(true);
        when(friendshipRepository.addFriendship(any(Friendship.class))).thenReturn(true);

        AddFriendCommand command = new AddFriendCommand(authenticator, friendshipRepository, userRepository, notificationRepository, tokens);
        String result = command.execute();

        assertEquals("friend added in friends", result);
        verify(friendshipRepository).save();
        verify(notificationRepository).addFriendNotification("friend", AddFriendNotification.get("user"));
        verify(notificationRepository).save();
    }
}
