package bg.sofia.uni.fmi.mjt.split.command;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.implementation.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.GetStatusCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.HelpCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.LogInCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.LogOutCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.PayedCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.PayedGroupCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.RegisterCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.SplitCommand;
import bg.sofia.uni.fmi.mjt.split.command.implementation.SplitGroupCommand;
import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.SplitService;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CommandCreatorTest {
    private static final String REGISTER = "register";
    private static final String LOG_IN = "log-in";
    private static final String LOG_OUT = "log-out";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String GET_STATUS = "get-status";
    private static final String PAYED = "payed";
    private static final String PAYED_GROUP = "payed-group";
    private static final String HELP = "help";
    private static final String UNKNOWN_COMMAND = "something else";

    @Mock
    SplitService splitService;

    @InjectMocks
    private CommandCreator commandCreator;

    @Test
    void testNewCommand() throws InvalidCommandFormatException {
        assertInstanceOf(RegisterCommand.class, commandCreator.newCommand(REGISTER));
        assertInstanceOf(LogInCommand.class, commandCreator.newCommand(LOG_IN));
        assertInstanceOf(LogOutCommand.class, commandCreator.newCommand(LOG_OUT));
        assertInstanceOf(AddFriendCommand.class, commandCreator.newCommand(ADD_FRIEND));
        assertInstanceOf(CreateGroupCommand.class, commandCreator.newCommand(CREATE_GROUP));
        assertInstanceOf(SplitCommand.class, commandCreator.newCommand(SPLIT));
        assertInstanceOf(SplitGroupCommand.class, commandCreator.newCommand(SPLIT_GROUP));
        assertInstanceOf(GetStatusCommand.class, commandCreator.newCommand(GET_STATUS));
        assertInstanceOf(PayedCommand.class, commandCreator.newCommand(PAYED));
        assertInstanceOf(PayedGroupCommand.class, commandCreator.newCommand(PAYED_GROUP));
        assertInstanceOf(HelpCommand.class, commandCreator.newCommand(HELP));
        assertThrows(InvalidCommandFormatException.class, () -> commandCreator.newCommand(UNKNOWN_COMMAND));
    }
}
