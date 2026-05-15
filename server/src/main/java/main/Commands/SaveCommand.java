package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import main.model.User;

public class SaveCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public SaveCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        collectionManager.save();
        return "Collection saved on server";
    }

    @Override
    public String toString() {
        return "save collection to file";
    }
}
