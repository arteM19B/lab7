package main.Commands;

import Network.CommandArgument;
import Network.IntegerArgument;
import main.db.ConnectionManager;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class RemoveAtCommand implements Command {
    private final CollectionService collectionService;

    public RemoveAtCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        if (!(argument instanceof IntegerArgument)) {
            return "Error: index argument is required";
        }

        int index = ((IntegerArgument) argument).getValue();
        if (index < 0 || index >= collectionService.size()) {
            return "Error: element with this index does not exist";
        }
        try {
            if (collectionService.removeAt(index, user)) {
                return "Element at index " + index + " removed";
            } else {
                return "Error: element with this index does not exist";
            }
        } catch (SQLException e) {
            return ConnectionManager.getDatabaseErrorMessage(e);
        }
    }

    @Override
    public String toString() {
        return "remove element at the given index";
    }
}
