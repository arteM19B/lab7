package main.Commands;

import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class RemoveAllByDistanceCommand implements Command {
    private final CollectionService collectionService;

    public RemoveAllByDistanceCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        if (!(argument instanceof LongArgument)) {
            return "Error: distance argument is required";
        }

        long distance = ((LongArgument) argument).getValue();
        if (distance <= 1) {
            return "Error: distance must be greater than 1";
        }
        try {
            int count = collectionService.removeAllByDistance(distance, user);
            if (count == 0) {
                return "No routes with distance " + distance + " found";
            }
            return "Removed " + count + " routes with distance " + distance;
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "remove all elements with distance equal to given value";
    }
}
