package bg.sofia.uni.fmi.mjt.split.server;

import bg.sofia.uni.fmi.mjt.split.authentication.Authenticator;
import bg.sofia.uni.fmi.mjt.split.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.split.exception.server.RepositoryException;
import bg.sofia.uni.fmi.mjt.split.repository.FriendshipRepository;
import bg.sofia.uni.fmi.mjt.split.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.split.repository.NotificationRepository;
import bg.sofia.uni.fmi.mjt.split.repository.SplitService;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitWiseServer {

    private static final int PORT = 2307;
    private static final String ERROR_FILE = "errorLog.txt";
    private static final String SYSTEM_FILE_DIRECTORY = "system";

    public static void main(String[] args) {
        SplitWiseServer server = new SplitWiseServer();
        server.start();
    }

    public SplitWiseServer() { }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            SplitService splitService = SplitService.builder()

                .groupRepository(new GroupRepository())
                .notificationRepository(new NotificationRepository())
                .friendshipRepository(new FriendshipRepository())
                .userRepository(new UserRepository())
                .build();
            System.out.println("SplitWiseServer is listening for incoming client requests");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " + clientSocket.getInetAddress() + " connected");
                CommandCreator commandCreator =
                    new CommandCreator(splitService, new Authenticator(splitService.getUserRepository()));
                SplitClientRequestHandler
                    clientRequestHandler = new SplitClientRequestHandler(clientSocket, commandCreator);
                executor.execute(clientRequestHandler);
            }
        } catch (IOException | RepositoryException e) {
            try (PrintWriter writer = new PrintWriter(Path.of(SYSTEM_FILE_DIRECTORY, ERROR_FILE).toString())) {
                e.printStackTrace(writer);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}
