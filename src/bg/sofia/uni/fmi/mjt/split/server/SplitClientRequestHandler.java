package bg.sofia.uni.fmi.mjt.split.server;

import bg.sofia.uni.fmi.mjt.split.command.CommandCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SplitClientRequestHandler implements Runnable {

    private final Socket socket;
    private final CommandCreator commandCreator;

    public SplitClientRequestHandler(Socket socket, CommandCreator commandCreator) {
        this.socket = socket;
        this.commandCreator = commandCreator;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("SplitClient Request Handler for " + socket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    out.println(commandCreator.newCommand(inputLine).execute());
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
