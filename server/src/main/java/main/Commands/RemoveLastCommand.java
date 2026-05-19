package main.Commands;

import Network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class RemoveLastCommand implements Command {
    private final CollectionService collectionService;

    public RemoveLastCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        try {
            boolean removed = collectionService.removeLast(user);

            if (!removed) {
                return "Collection is empty or last element does not belong to you";
            }

            return "Last element removed";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

}
