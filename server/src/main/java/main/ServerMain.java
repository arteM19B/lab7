package main;

import main.collectionManager.Invoker;
import main.commands.*;
import network.CommandType;
import main.serverNetwork.ReceivedPacket;
import main.serverNetwork.RequestReader;
import main.serverNetwork.RequestDispatcher;
import main.serverNetwork.ResponseSender;
import main.serverNetwork.UdpRequestReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;

import main.db.CollectionDAO;
import main.db.ConnectionManager;
import main.db.UserDAO;
import main.service.AuthService;
import main.service.CollectionService;
import main.service.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {
    private static final int PORT = 1234;
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        logger.info("Server started");

        UdpRequestReceiver requestReceiver = new UdpRequestReceiver();
        RequestReader requestReader = new RequestReader();
        ResponseSender responseSender = new ResponseSender();

        DatabaseConfig databaseConfig;
        try {
            databaseConfig = loadDatabaseConfig();
        } catch (Exception e) {
            System.err.println("Database config error: " + e.getMessage());
            logger.error("Database config error: {}", e.getMessage());
            return;
        }

        ConnectionManager connectionManager = new ConnectionManager(
                databaseConfig.url(),
                databaseConfig.user(),
                databaseConfig.password()
        );
        UserDAO userDAO = new UserDAO(connectionManager);
        AuthService authService = new AuthService(userDAO);
        Invoker invoker = new Invoker();
        CollectionDAO collectionDAO = new CollectionDAO(connectionManager);
        CollectionService collectionService = new CollectionService(collectionDAO);
        registerCommands(invoker, collectionService);
        RequestHandler requestHandler = new RequestHandler(authService, invoker);
        RequestDispatcher requestDispatcher = new RequestDispatcher(requestReader, requestHandler, responseSender);

        try {
            logger.info("Loading collection from database");
            collectionService.loadFromDatabase();
            logger.info("Collection loaded from database");
        } catch (SQLException e) {
            String message = ConnectionManager.getDatabaseErrorMessage(e);
            System.err.println(message);
            logger.error("Error while loading collection from database: {}", ConnectionManager.getDatabaseLogMessage(e));
        }


        try (DatagramChannel channel = DatagramChannel.open();
             requestDispatcher) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(PORT));
            logger.info("Server started on port: {}", PORT);

            ByteBuffer buffer = ByteBuffer.allocate(65536);

            while (true) {
                ReceivedPacket receivedPacket = requestReceiver.receive(channel, buffer);
                if (receivedPacket != null) {
                    logger.info("Packet received from: {}", receivedPacket.getClientAddress());
                    requestDispatcher.dispatch(channel, receivedPacket);
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            String message = toServerErrorMessage(e);
            System.err.println("Server error: " + message);
            logger.error("Server error: {}", message);
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

    private static DatabaseConfig loadDatabaseConfig() throws IOException {
        Path configPath = findDatabaseConfigPath();
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
        }

        return new DatabaseConfig(
                requireProperty(properties, "db.url"),
                requireProperty(properties, "db.user"),
                requireProperty(properties, "db.password")
        );
    }

    private static Path findDatabaseConfigPath() {
        Path rootConfig = Path.of("server.properties");
        if (Files.exists(rootConfig)) {
            return rootConfig;
        }

        Path serverConfig = Path.of("server", "server.properties");
        if (Files.exists(serverConfig)) {
            return serverConfig;
        }

        throw new IllegalStateException("server.properties was not found");
    }

    private static String requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Property " + key + " is not set");
        }
        return value.trim();
    }

    private static String toServerErrorMessage(Exception exception) {
        if (exception instanceof BindException) {
            return "port " + PORT + " is already in use";
        }

        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? exception.getClass().getSimpleName()
                : message;
    }

    private record DatabaseConfig(String url, String user, String password) {
    }
}
