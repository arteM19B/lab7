package main.Commands;

import Collection.Route;
import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.UpdateArgument;
import main.model.User;

public class UpdateCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public UpdateCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        if (!(argument instanceof UpdateArgument)) {
            return "Error: update needs id and Route arguments";
        }

        UpdateArgument updateArgument = (UpdateArgument) argument;
        Long id = updateArgument.getId();
        Route<Long> newRoute = updateArgument.getRoute();

        if (id == null || id <= 0 || newRoute == null) {
            return "Error: id or route was not received";
        }

        Route<Long> existing = collectionManager.getById(id);
        if (existing == null) {
            return "Route with ID " + id + " not found";
        }

        newRoute.setId(id);
        collectionManager.update(id, newRoute);
        return "Route with ID " + id + " updated";
    }

    @Override
    public String toString() {
        return "update collection element by id";
    }
}
