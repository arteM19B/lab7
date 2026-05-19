package main.Commands;

import Collection.Route;
import Network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

import java.util.stream.Collectors;

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
