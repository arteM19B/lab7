package main.commands;

import network.CommandArgument;
import network.CommandType;
import main.model.User;

import java.util.Map;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    private final Map<CommandType, Command> commands;

    public HelpCommand(Map<CommandType, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        return commands.entrySet().stream()
                .map(entry -> entry.getKey().getUserName() + " -- " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return "show available client commands";
    }
}
