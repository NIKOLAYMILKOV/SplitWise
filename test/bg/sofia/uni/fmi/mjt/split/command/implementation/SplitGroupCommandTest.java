package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.Friendship;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupException;
import bg.sofia.uni.fmi.mjt.split.notification.SplitRequestNotification;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplitGroupCommandTest {
    private static final String AUTHENTICATED_USERNAME = "testUser";
    private static final String FRIEND_USERNAME = "friendUser";
    private static final double AMOUNT = 10.0;
    private static final String PAYMENT_REASON = "Dinner";
    private static final String GROUP_NAME = "TestGroup";

    private Authenticator authenticatorMock;
    private FriendshipRepository friendshipRepositoryMock;
    private GroupRepository groupRepositoryMock;
    private NotificationRepository notificationRepositoryMock;
    private User authenticatedUserMock;
    private Group groupMock;

    @BeforeEach
    void setUp() {
        authenticatorMock = mock(Authenticator.class);
        friendshipRepositoryMock = mock(FriendshipRepository.class);
        groupRepositoryMock = mock(GroupRepository.class);
        notificationRepositoryMock = mock(NotificationRepository.class);
        authenticatedUserMock = mock(User.class);
        groupMock = mock(Group.class);
    }

    @Test
    void testExecuteSuccessfulSplit() throws InvalidCommandFormatException {
        String[] tokens = {"10", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticatorMock.isAuthenticated()).thenReturn(true);
        when(authenticatorMock.getAuthenticatedUser()).thenReturn(authenticatedUserMock);
        when(authenticatedUserMock.username()).thenReturn(AUTHENTICATED_USERNAME);
        when(friendshipRepositoryMock.areFriends(AUTHENTICATED_USERNAME, FRIEND_USERNAME)).thenReturn(true);
        var friendship = mock(Friendship.class);
        when(friendshipRepositoryMock.getFriendshipByUsernames(AUTHENTICATED_USERNAME, FRIEND_USERNAME))
            .thenReturn(Optional.of(friendship));

        SplitCommand
            command = new SplitCommand(authenticatorMock, friendshipRepositoryMock, notificationRepositoryMock, tokens);

        String result = command.execute();

        verify(friendship).split(AUTHENTICATED_USERNAME, AMOUNT);
        verify(friendshipRepositoryMock).save();
        verify(notificationRepositoryMock).addFriendNotification(FRIEND_USERNAME,
            SplitRequestNotification.get(AUTHENTICATED_USERNAME, AMOUNT / 2, PAYMENT_REASON));
        verify(notificationRepositoryMock).save();
        assertEquals("Split request is successfully sent", result);
    }

    @Test
    void testExecuteThrowsUserNotLoggedInException() {
        String[] tokens = {"10", FRIEND_USERNAME, PAYMENT_REASON};
        when(authenticatorMock.isAuthenticated()).thenReturn(false);

        SplitCommand command = new SplitCommand(authenticatorMock, friendshipRepositoryMock, notificationRepositoryMock, tokens);

        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteGroupSplitSuccessful() throws InvalidCommandFormatException {
        String[] tokens = {"10", GROUP_NAME, PAYMENT_REASON};
        when(authenticatorMock.isAuthenticated()).thenReturn(true);
        when(authenticatorMock.getAuthenticatedUser()).thenReturn(authenticatedUserMock);
        when(authenticatedUserMock.username()).thenReturn(AUTHENTICATED_USERNAME);
        when(groupRepositoryMock.getGroupByNameAndMember(GROUP_NAME, AUTHENTICATED_USERNAME))
            .thenReturn(Optional.of(groupMock));
        when(groupMock.name()).thenReturn(GROUP_NAME);
        when(groupMock.members()).thenReturn(Set.of(AUTHENTICATED_USERNAME, FRIEND_USERNAME));

        SplitGroupCommand command = new SplitGroupCommand(authenticatorMock, groupRepositoryMock,
            notificationRepositoryMock, tokens);

        String result = command.execute();

        verify(groupMock).split(AUTHENTICATED_USERNAME, AMOUNT);
        verify(groupRepositoryMock).save();
        verify(notificationRepositoryMock).addGroupNotification(FRIEND_USERNAME, GROUP_NAME,
            SplitRequestNotification.get(AUTHENTICATED_USERNAME, AMOUNT / 2, PAYMENT_REASON));
        verify(notificationRepositoryMock).save();
        assertEquals("Split request is successfully sent", result);
    }

    @Test
    void testExecuteGroupSplitThrowsGroupException() {
        String[] tokens = {"10", GROUP_NAME, PAYMENT_REASON};
        when(authenticatorMock.isAuthenticated()).thenReturn(true);
        when(authenticatorMock.getAuthenticatedUser()).thenReturn(authenticatedUserMock);
        when(authenticatedUserMock.username()).thenReturn(AUTHENTICATED_USERNAME);
        when(groupRepositoryMock.getGroupByNameAndMember(GROUP_NAME, AUTHENTICATED_USERNAME)).thenReturn(Optional.empty());

        SplitGroupCommand command = new SplitGroupCommand(authenticatorMock, groupRepositoryMock,
            notificationRepositoryMock, tokens);

        assertThrows(GroupException.class, command::execute);
    }
}
