package main;

import main.CollectionManager.CollectionManager;
import main.CollectionManager.Invoker;
import main.Commands.*;
import Network.CommandType;
import Network.Request;
import Network.Response;
import main.ServerNetwork.ReceivedPacket;
import main.ServerNetwork.RequestReader;
import main.ServerNetwork.ResponseSender;
import main.ServerNetwork.UdpRequestReceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

import main.db.CollectionDAO;
import main.db.ConnectionManager;
import main.db.UserDAO;
import main.service.AuthService;
import main.service.CollectionService;
import main.service.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ServerMain {
    private static final int PORT = 1234;
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    private static final String dbLogin = "s501442";
    private static final String dbPassword = "...";
    private static final String dbUrl = "jdbc:postgresql://localhost:15432/studs";

    public static void main(String[] args) {
        logger.info("Server started");

        UdpRequestReceiver requestReceiver = new UdpRequestReceiver();
        RequestReader requestReader = new RequestReader();
        ResponseSender responseSender = new ResponseSender();


        ConnectionManager connectionManager = new ConnectionManager(dbUrl, dbLogin, dbPassword);
        UserDAO userDAO = new UserDAO(connectionManager);
        AuthService authService = new AuthService(userDAO);
        Invoker invoker = new Invoker();
        CollectionDAO collectionDAO = new CollectionDAO(connectionManager);
        CollectionService collectionService = new CollectionService(collectionDAO);
        registerCommands(invoker, collectionService);
        RequestHandler requestHandler = new RequestHandler(authService, invoker);

        try {
            logger.info("Loading collection from database");
            collectionService.loadFromDatabase();
            logger.info("Collection loaded from database");
        } catch (SQLException e) {
            logger.error("Error while loading collection from database", e);
        }


        try (DatagramChannel channel = DatagramChannel.open();
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(PORT));
            logger.info("Server started on port: {}", PORT);

            ByteBuffer buffer = ByteBuffer.allocate(65536);
            boolean running = true;

            while (true) {
                ReceivedPacket receivedPacket = requestReceiver.receive(channel, buffer);
                if (receivedPacket != null) {
                    try {
                        logger.info("Packet received from: {}", receivedPacket.getClientAddress());
                        Request request = requestReader.read(receivedPacket.getData());
                        MDC.put("requestId", request.getRequestId());
                        logger.info("Request received: {}", request.getRequestId());
                        Response response = requestHandler.handle(request);
                        logger.info("Response prepared for request: {}", request.getRequestId());
                        responseSender.send(channel, receivedPacket, response);
                        logger.info("Response sent for request: {}", request.getRequestId());
                    } catch (Exception e) {
                        logger.error("Error while processing request", e);
                    } finally {
                        MDC.clear();
                    }
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            logger.error("Server error", e);
        } finally {
            logger.info("Server stopped");
        }
    }

    private static void registerCommands(Invoker invoker, CollectionService collectionService) {
        invoker.registerCommand(CommandType.HELP, new HelpCommand(invoker.getCommandsMap()));
        invoker.registerCommand(CommandType.INFO, new InfoCommand(collectionService));
        invoker.registerCommand(CommandType.SHOW, new ShowCommand(collectionService));
        invoker.registerCommand(CommandType.ADD, new AddCommand(collectionService));
        invoker.registerCommand(CommandType.UPDATE, new UpdateCommand(collectionService));
        invoker.registerCommand(CommandType.REMOVE_BY_ID, new RemoveIdCommand(collectionService));
        invoker.registerCommand(CommandType.REMOVE_AT, new RemoveAtCommand(collectionService));
        invoker.registerCommand(CommandType.CLEAR, new ClearCommand(collectionService));
        invoker.registerCommand(CommandType.REMOVE_LAST, new RemoveLastCommand(collectionService));
        invoker.registerCommand(CommandType.SORT, new SortCommand(collectionService));
        invoker.registerCommand(CommandType.REMOVE_ALL_BY_DISTANCE, new RemoveAllByDistanceCommand(collectionService));
        invoker.registerCommand(CommandType.COUNT_GREATER_THAN_DISTANCE, new CounGreaterThanDistanceCommand(collectionService));
        invoker.registerCommand(CommandType.FILTER_LESS_THAN_DISTANCE, new FilterLessThanDistanceCommand(collectionService));
        logger.info("Commands registered");
    }

    private static boolean handleServerCommand(String line, CollectionManager<Long> collectionManager) {
        String command = line == null ? "" : line.trim();
        if (command.isEmpty()) {
            return true;
        }

        if (command.equals("exit")) {
            logger.info("Server console command received: exit");
            return false;
        }

        System.out.println("Server command is not available: " + command);
        System.out.println("Available server commands: save, exit");
        logger.warn("Unknown server console command: {}", command);

        return true;
    }
}
