package main.CollectionManager;

import main.Commands.Command;
import Network.CommandType;
import Network.Request;
import main.model.User;

import java.util.HashMap;
import java.util.Map;

public class Invoker {
    private final Map<CommandType, Command> commandsMap = new HashMap<>();

    public void registerCommand(CommandType type, Command command) {
        commandsMap.put(type, command);
    }

    public Command getCommand(CommandType type) {
        return commandsMap.get(type);
    }

    public Map<CommandType, Command> getCommandsMap() {
        return commandsMap;
    }

    public String execute(Request request, User user) {
        Command command = commandsMap.get(request.getCommandType());

        if (command == null) {
            return "Unknown command: " + request.getCommandType().getUserName();
        }

        try {
            return command.execute(request.getCommandArgument(), user);
        } catch (Exception e) {
            return "Command execution error: " + e.getMessage();
        }
    }
}
