package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;

public class RemoveAllByDistanceCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public RemoveAllByDistanceCommand(CollectionManager<Long> collectionManager) {
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

        int count = collectionManager.removeAllByDistance(distance);
        if (count == 0) {
            return "No routes with distance " + distance + " found";
        }
        return "Removed " + count + " routes with distance " + distance;
    }

    @Override
    public String toString() {
        return "remove all elements with distance equal to given value";
    }
}
