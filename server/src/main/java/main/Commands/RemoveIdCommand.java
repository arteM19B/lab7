package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import Network.LongArgument;
import main.model.User;

public class RemoveIdCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public RemoveIdCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        if (!(argument instanceof LongArgument)) {
            return "Error: id argument is required";
        }

        Long id = ((LongArgument) argument).getValue();
        boolean found = collectionManager.getCollection().removeIf(route -> id.equals(route.getId()));
        return found ? "Element with ID " + id + " removed" : "Element with ID " + id + " not found";
    }

    @Override
    public String toString() {
        return "remove element by id";
    }
}
