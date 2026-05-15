package main.Commands;

import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;
import main.service.CollectionService;

public class FilterLessThanDistanceCommand implements Command {
    private final CollectionService collectionService;

    public FilterLessThanDistanceCommand(CollectionService collectionService) {
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

        return collectionService.filterLessThanDistance(distance);
    }

    @Override
    public String toString() {
        return "show elements with distance less than given value";
    }
}
