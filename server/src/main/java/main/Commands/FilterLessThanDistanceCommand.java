package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;

public class FilterLessThanDistanceCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public FilterLessThanDistanceCommand(CollectionManager<Long> collectionManager) {
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
        return collectionManager.filterLessThanDistance(distance);
    }

    @Override
    public String toString() {
        return "show elements with distance less than given value";
    }
}
