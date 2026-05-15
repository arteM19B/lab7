package main.Commands;

import Collection.Route;
import Network.CommandArgument;
import main.model.User;
import main.service.CollectionService;

import java.util.Comparator;
import java.util.stream.Collectors;

public class SortCommand implements Command {
    private final CollectionService collectionService;

    public SortCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument, User user) {
        return collectionService.snapshot().stream()
                .sorted()
                .map(Route::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return "show collection elements sorted by natural order";
    }
}
