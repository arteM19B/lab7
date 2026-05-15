package main;

import Collection.RouteXMLParser;
import Exceptions.ScriptExecutionException;
import Network.CommandArgument;
import Network.CommandType;
import Network.IntegerArgument;
import Network.LongArgument;
import Network.NoArgument;
import Network.RouteArgument;
import Network.UpdateArgument;

import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ClientCommandExecutor.class);

    private final ClientRequestSender sender;
    private ExecuteScriptCommand executeScriptCommand;

    public ClientCommandExecutor(ClientRequestSender sender) {
        this.sender = sender;
    }

    public void setExecuteScriptCommand(ExecuteScriptCommand executeScriptCommand) {
        this.executeScriptCommand = executeScriptCommand;
    }

    public void executeInteractiveLine(String line, RouteBuilder routeBuilder, String login, String password) {
        ParsedCommand parsedCommand = parse(line);
        if (parsedCommand == null) {
            return;
        }

        if (parsedCommand.name.equals("execute_script")) {
            executeScriptCommand.execute(parsedCommand.argument, login, password);
            return;
        }

        if (parsedCommand.name.equals("save")) {
            System.out.println("Command save is available only on server");
            return;
        }

        Optional<CommandType> commandType = CommandType.fromUserName(parsedCommand.name);
        if (!commandType.isPresent()) {
            logger.warn("Unknown command: {}", parsedCommand.name);
            return;
        }

        CommandArgument argument = buildInteractiveArgument(commandType.get(), parsedCommand.argument, routeBuilder);
        if (argument == null) {
            return;
        }

        try {
            String response = sender.send(commandType.get(), argument, login, password);
            System.out.println(response);
            printClientHelp(commandType.get());
        } catch (SocketTimeoutException e) {
            System.err.println("Server is temporarily unavailable. Try again later.");
            logger.warn("Server response timeout");
        } catch (Exception e) {
            System.err.println("Could not process server response: " + e.getMessage());
            logger.error("Network or serialization error");
        }
    }

    public void executeScriptLine(String line, Scanner scriptScanner, String scriptName, int lineNumber, String login, String password) {
        ParsedCommand parsedCommand = parse(line);
        if (parsedCommand == null) {
            return;
        }

        if (parsedCommand.name.equals("execute_script")) {
            executeScriptCommand.execute(parsedCommand.argument, login, password);
            return;
        }



        if (parsedCommand.name.equals("save") || parsedCommand.name.equals("exit")) {
            throw new ScriptExecutionException("command is available only outside scripts", scriptName, lineNumber, parsedCommand.name);
        }

        Optional<CommandType> commandType = CommandType.fromUserName(parsedCommand.name);
        if (!commandType.isPresent()) {
            System.out.println("Unknown command in script: " + parsedCommand.name);
            return;
        }

        try {
            CommandArgument argument = buildScriptArgument(commandType.get(), parsedCommand.argument, scriptScanner);
            String response = sender.send(commandType.get(), argument, login, password);
            System.out.println(response);
        } catch (ScriptExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ScriptExecutionException(e.getMessage(), scriptName, lineNumber, parsedCommand.name);
        }
    }

    private CommandArgument buildInteractiveArgument(
            CommandType commandType,
            String rawArgument,
            RouteBuilder routeBuilder
    ) {
        switch (commandType) {
            case ADD:
                return new RouteArgument(routeBuilder.build());
            case UPDATE:
                Long updateId = parseLongArgument(rawArgument, "id");
                if (updateId == null || updateId <= 0) {
                    if (updateId != null) {
                        System.out.println("Error: id must be greater than 0");
                    }
                    return null;
                }
                return new UpdateArgument(updateId, routeBuilder.build());
            default:
                return buildSimpleArgument(commandType, rawArgument);
        }
    }

    private CommandArgument buildScriptArgument(
            CommandType commandType,
            String rawArgument,
            Scanner scriptScanner
    ) {
        switch (commandType) {
            case ADD:
                return new RouteArgument(RouteXMLParser.parse(scriptScanner));
            case UPDATE:
                Long updateId = parseLongArgument(rawArgument, "id");
                if (updateId == null || updateId <= 0) {
                    throw new IllegalArgumentException("id must be greater than 0");
                }
                return new UpdateArgument(updateId, RouteXMLParser.parse(scriptScanner));
            default:
                CommandArgument argument = buildSimpleArgument(commandType, rawArgument);
                if (argument == null) {
                    throw new IllegalArgumentException("invalid command argument");
                }
                return argument;
        }
    }

    private CommandArgument buildSimpleArgument(CommandType commandType, String rawArgument) {
        switch (commandType) {
            case REMOVE_BY_ID:
                Long id = parseLongArgument(rawArgument, "id");
                if (id != null && id <= 0) {
                    System.out.println("Error: id must be greater than 0");
                    return null;
                }
                return id == null ? null : new LongArgument(id);
            case REMOVE_AT:
                Integer index = parseIntegerArgument(rawArgument, "index");
                if (index != null && index < 0) {
                    System.out.println("Error: index must be greater than or equal to 0");
                    return null;
                }
                return index == null ? null : new IntegerArgument(index);
            case REMOVE_ALL_BY_DISTANCE:
            case COUNT_GREATER_THAN_DISTANCE:
            case FILTER_LESS_THAN_DISTANCE:
                Long distance = parseLongArgument(rawArgument, "distance");
                if (distance != null && distance <= 1) {
                    System.out.println("Error: distance must be greater than 1");
                    return null;
                }
                return distance == null ? null : new LongArgument(distance);
            default:
                return new NoArgument();
        }
    }

    private Long parseLongArgument(String rawArgument, String name) {
        if (rawArgument == null || rawArgument.isEmpty()) {
            System.out.println("Error: " + name + " argument is required");
            return null;
        }
        try {
            return Long.parseLong(rawArgument);
        } catch (NumberFormatException e) {
            System.out.println("Error: " + name + " must be a number");
            logger.warn("Invalid number argument '{}': {}", name, rawArgument);
            return null;
        }
    }

    private Integer parseIntegerArgument(String rawArgument, String name) {
        if (rawArgument == null || rawArgument.isEmpty()) {
            System.out.println("Error: " + name + " argument is required");
            return null;
        }
        try {
            return Integer.parseInt(rawArgument);
        } catch (NumberFormatException e) {
            System.out.println("Error: " + name + " must be an integer number");
            logger.warn("Invalid integer argument '{}': {}", name, rawArgument);
            return null;
        }
    }

    private ParsedCommand parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase(Locale.ROOT);
        String rawArgument = parts.length > 1 ? parts[1].trim() : null;
        return new ParsedCommand(commandName, rawArgument);
    }

    private void printClientHelp(CommandType commandType) {
        if (commandType == CommandType.HELP) {
            System.out.println("execute_script file_name : read and execute commands from the specified file");
        }
    }

    private static class ParsedCommand {
        private final String name;
        private final String argument;

        private ParsedCommand(String name, String argument) {
            this.name = name;
            this.argument = argument;
        }
    }
}
