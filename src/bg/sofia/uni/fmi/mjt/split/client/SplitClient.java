package bg.sofia.uni.fmi.mjt.split.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SplitClient {
    private static final int SERVER_PORT = 2307;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("SplitWiseClientThread " + socket.getLocalPort());
            System.out.println("Welcome to SplitWise");
            String message;
            while (true) {
                System.out.print("> ");
                message = scanner.nextLine();

                if ("quit".equals(message)) {
                    break;
                }

                writer.println(message);
                String reply;
                do {
                    reply = reader.readLine();
                    System.out.println(reply);
                } while (reader.ready());
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication. Please try again later");
        }
    }
}