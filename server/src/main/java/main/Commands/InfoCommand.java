package main.Commands;

import Network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

public class InfoCommand implements Command {
    private final CollectionService collectionService;

    public InfoCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        return collectionService.info();
    }

    @Override
    public String toString() {
        return "show collection information";
    }
}
