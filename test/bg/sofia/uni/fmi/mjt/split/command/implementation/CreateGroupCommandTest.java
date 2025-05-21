package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.registration.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateGroupCommandTest {

    private static final String USERNAME = "testUser";
    private static final String GROUP_NAME = "testGroup";
    private static final String OTHER_USER = "otherUser";
    private static final String THIRD_USER = "thirdUser";

    @Mock
    private Authenticator authenticator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.username()).thenReturn(USERNAME);
    }

    @Test
    void testExecuteSuccess() throws InvalidCommandFormatException {
        String[] tokens = {GROUP_NAME, OTHER_USER, THIRD_USER};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);
        when(userRepository.isRegistered(USERNAME)).thenReturn(true);
        when(userRepository.isRegistered(OTHER_USER)).thenReturn(true);
        when(userRepository.isRegistered(THIRD_USER)).thenReturn(true);
        when(groupRepository.addGroup(any(Group.class))).thenReturn(true);

        CreateGroupCommand
            command = new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, tokens);
        String result = command.execute();

        assertEquals("Group " + GROUP_NAME + " successfully created", result);
        verify(groupRepository).save();
        verify(notificationRepository).save();
    }

    @Test
    void testExecuteFailsWhenNotLoggedIn() {
        String[] tokens = {GROUP_NAME, OTHER_USER, THIRD_USER};
        when(authenticator.isAuthenticated()).thenReturn(false);

        CreateGroupCommand command = new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, tokens);
        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenTooFewMembers() {
        String[] tokens = {GROUP_NAME, OTHER_USER};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);

        CreateGroupCommand command = new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenUserNotRegistered() {
        String[] tokens = {GROUP_NAME, OTHER_USER, THIRD_USER};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);
        when(userRepository.isRegistered(USERNAME)).thenReturn(true);
        when(userRepository.isRegistered(OTHER_USER)).thenReturn(false);

        CreateGroupCommand command = new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, tokens);
        assertThrows(UserNotRegisteredException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenGroupAlreadyExists() {
        String[] tokens = {GROUP_NAME, OTHER_USER, THIRD_USER};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);
        when(userRepository.isRegistered(USERNAME)).thenReturn(true);
        when(userRepository.isRegistered(OTHER_USER)).thenReturn(true);
        when(userRepository.isRegistered(THIRD_USER)).thenReturn(true);
        when(groupRepository.addGroup(any(Group.class))).thenReturn(false);

        CreateGroupCommand command = new CreateGroupCommand(authenticator, userRepository, groupRepository, notificationRepository, tokens);
        assertThrows(GroupAlreadyExistsException.class, command::execute);
    }
}