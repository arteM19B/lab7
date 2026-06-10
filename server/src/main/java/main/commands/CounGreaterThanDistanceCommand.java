package main.commands;

import network.CommandArgument;
import network.LongArgument;
import main.model.User;
import main.service.CollectionService;

public class CounGreaterThanDistanceCommand implements Command {
    private final CollectionService collectionService;

    public CounGreaterThanDistanceCommand(CollectionService collectionService) {
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

        int count = collectionService.countGreaterThanDistance(distance);
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
