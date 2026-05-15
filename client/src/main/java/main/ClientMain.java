package main;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {
    private static final int SERVER_TIMEOUT_MS = 30000;
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RouteBuilder routeBuilder = new RouteBuilder(scanner, true);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(SERVER_TIMEOUT_MS);
            InetAddress address = InetAddress.getByName("localhost");
            int port = 1234;
            ClientRequestSender sender = new ClientRequestSender(socket, address, port);
            ClientCommandExecutor commandExecutor = new ClientCommandExecutor(sender);
            ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(commandExecutor);
            commandExecutor.setExecuteScriptCommand(executeScriptCommand);

            logger.info("Client started");

            System.out.println("Введите логин и пароль пользователя\n");

            System.out.println("Логин: ");
            String login = scanner.nextLine();

            System.out.println("Пароль: ");
            String password = scanner.nextLine();

            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String commandName = line.split("\\s+", 2)[0];
                if (commandName.equalsIgnoreCase("exit")) {
                    break;
                }

                commandExecutor.executeInteractiveLine(line, routeBuilder, login, password);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        logger.info("Client stopped");
    }
}
