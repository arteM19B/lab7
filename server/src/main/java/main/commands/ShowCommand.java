package main.commands;

import network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

public class ShowCommand implements Command {
    private final CollectionService collectionService;

    public ShowCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        return collectionService.show();
    }

    @Override
    public String toString() {
        return "show all collection elements sorted by location";
    }
}
