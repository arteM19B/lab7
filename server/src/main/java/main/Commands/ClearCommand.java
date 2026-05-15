package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import main.model.User;

public class ClearCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public ClearCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        collectionManager.clear();
        return "Collection cleared";
    }

    @Override
    public String toString() {
        return "clear collection";
    }
}
