package main.Commands;

import Network.CommandArgument;
import Network.LongArgument;
import main.db.ConnectionManager;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class RemoveIdCommand implements Command {
    private final CollectionService collectionService;

    public RemoveIdCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        if (!(argument instanceof LongArgument)) {
            return "Error: id argument is required";
        }

        Long id = ((LongArgument) argument).getValue();
        try {
            boolean found = collectionService.removeById(id, user);
            return found ? "Element with ID " + id + " removed" : "Element with ID " + id + " not found";
        } catch (SQLException e) {
            return ConnectionManager.getDatabaseErrorMessage(e);
        }
    }

    @Override
    public String toString() {
        return "remove element by id";
    }
}
