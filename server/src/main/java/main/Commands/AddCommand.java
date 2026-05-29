package main.Commands;

import Collection.Route;
import Network.CommandArgument;
import Network.RouteArgument;
import main.db.ConnectionManager;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class AddCommand implements Command {
    private final CollectionService collectionService;

    public AddCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public String execute(CommandArgument argument,  User user) {
        if (!(argument instanceof RouteArgument)) {
            return "Error: add needs Route argument";
        }

        Route<Long> route = ((RouteArgument) argument).getRoute();

        try {
            Route<Long> inserted = collectionService.add(route, user);
            return "Route added with ID " + inserted.getId();
        } catch (SQLException e) {
            return ConnectionManager.getDatabaseErrorMessage(e);
        }
    }

    @Override
    public String toString() {
        return "add new element to collection";
    }
}
