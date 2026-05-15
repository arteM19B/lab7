package main.Commands;

import Collection.Route;
import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.RouteArgument;
import main.model.User;

public class AddCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public AddCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument,  User user) {
        if (!(argument instanceof RouteArgument)) {
            return "Error: add needs Route argument";
        }

        Route<Long> route = ((RouteArgument) argument).getRoute();
        route.setId(collectionManager.generateNextId());
        collectionManager.add(route);
        return "Route added";
    }

    @Override
    public String toString() {
        return "add new element to collection";
    }
}
