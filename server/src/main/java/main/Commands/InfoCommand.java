package main.Commands;

import main.CollectionManager.CollectionManager;
import Network.CommandArgument;
import main.model.User;

public class InfoCommand implements Command {
    private final CollectionManager<Long> collectionManager;

    public InfoCommand(CollectionManager<Long> collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Collection type: ").append(collectionManager.getType()).append("\n");
        sb.append("Initialization time: ").append(collectionManager.getInitializationTime()).append("\n");
        sb.append("Elements count: ").append(collectionManager.size()).append("\n");

        if (collectionManager.getFileName() != null) {
            sb.append("Storage file: ").append(collectionManager.getFileName()).append("\n");
        }
        if (collectionManager.size() > 0) {
            sb.append("Element type: ").append(collectionManager.getCollection().getFirst().getClass().getSimpleName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "show collection information";
    }
}
