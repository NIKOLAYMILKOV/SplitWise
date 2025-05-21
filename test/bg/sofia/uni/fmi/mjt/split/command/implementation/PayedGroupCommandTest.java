package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.dto.Group;
import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.exception.user.authentication.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.UserNotGroupMemberException;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PayedGroupCommandTest {

    private static final String USERNAME = "testUser";
    private static final String GROUP_NAME = "testGroup";
    private static final String PAYER_USERNAME = "payerUser";
    private static final String AMOUNT = "100.0";

    @Mock
    private Authenticator authenticator;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private User mockUser;
    @Mock
    private Group mockGroup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.username()).thenReturn(USERNAME);
        when(authenticator.getAuthenticatedUser()).thenReturn(mockUser);
    }

    @Test
    void testExecuteSuccess() throws InvalidCommandFormatException {
        String[] tokens = {AMOUNT, GROUP_NAME, PAYER_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(groupRepository.getGroupByNameAndMember(GROUP_NAME, USERNAME)).thenReturn(Optional.of(mockGroup));
        when(mockGroup.isMember(PAYER_USERNAME)).thenReturn(true);

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        String result = command.execute();

        verify(mockGroup).pay(USERNAME, PAYER_USERNAME, Double.parseDouble(AMOUNT));
        verify(groupRepository).save();
        verify(notificationRepository).addGroupNotification(eq(PAYER_USERNAME), eq(GROUP_NAME), any());
        verify(notificationRepository).save();

        assertEquals("Successful payment", result);
    }

    @Test
    void testExecuteFailsWhenNotLoggedIn() {
        String[] tokens = {AMOUNT, GROUP_NAME, PAYER_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(false);

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        assertThrows(UserNotLoggedInException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenGroupNotFound() {
        String[] tokens = {AMOUNT, GROUP_NAME, PAYER_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(groupRepository.getGroupByNameAndMember(GROUP_NAME, USERNAME)).thenReturn(Optional.empty());

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        assertThrows(GroupException.class, command::execute);
    }

    @Test
    void testExecuteFailsWhenPayerNotMember() {
        String[] tokens = {AMOUNT, GROUP_NAME, PAYER_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);
        when(groupRepository.getGroupByNameAndMember(GROUP_NAME, USERNAME)).thenReturn(Optional.of(mockGroup));
        when(mockGroup.isMember(PAYER_USERNAME)).thenReturn(false);

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        assertThrows(UserNotGroupMemberException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithInvalidAmountFormat() {
        String[] tokens = {"invalidAmount", GROUP_NAME, PAYER_USERNAME};
        when(authenticator.isAuthenticated()).thenReturn(true);

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }

    @Test
    void testExecuteFailsWithMissingTokens() {
        String[] tokens = {AMOUNT, GROUP_NAME};
        when(authenticator.isAuthenticated()).thenReturn(true);

        PayedGroupCommand command = new PayedGroupCommand(authenticator, groupRepository, notificationRepository, tokens);
        assertThrows(InvalidCommandFormatException.class, command::execute);
    }
}
