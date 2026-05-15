package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;

public class CounGreaterThanDistanceCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public CounGreaterThanDistanceCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
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

        int count = collectionManager.countGreaterThanDistance(distance);
        if (count == 0) {
            return "No routes with distance greater than " + distance + " found";
        }
        return "Found " + count + " routes with distance greater than " + distance;
    }

    @Override
    public String toString() {
        return "count elements with distance greater than given value";
    }
}
