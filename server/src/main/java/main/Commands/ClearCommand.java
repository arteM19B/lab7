package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class ClearCommand implements Command {
    private final CollectionService collectionService;

    public ClearCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        try {
            collectionService.clearOwned(user);
            return "Collection cleared";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "clear collection";
    }
}
