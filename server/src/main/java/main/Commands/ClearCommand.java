package main.Commands;

import Network.CommandArgument;
import main.db.ConnectionManager;
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
            return ConnectionManager.getDatabaseErrorMessage(e);
        }
    }

    @Override
    public String toString() {
        return "clear collection";
    }
}
