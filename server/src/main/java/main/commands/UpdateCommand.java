package main.commands;

import collection.Route;
import network.CommandArgument;
import network.UpdateArgument;
import main.db.ConnectionManager;
import main.model.User;
import main.service.CollectionService;

import java.sql.SQLException;

public class UpdateCommand implements Command {
    private final CollectionService collectionService;

    public UpdateCommand(CollectionService collectionService) {
        this.collectionService = collectionService;
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

        Route<Long> existing = collectionService.getById(id);

        if (existing == null) {
            return "Route with ID " + id + " not found";
        }

        newRoute.setId(id);

        try {
            if (collectionService.update(id, newRoute, user)) {
                return "Route with ID " + id + " updated";
            } else {
                return "Error: you are trying to update a route that doesn't exist";
            }
        } catch (SQLException e) {
            return ConnectionManager.getDatabaseErrorMessage(e);
        }
    }

    @Override
    public String toString() {
        return "update collection element by id";
    }
}
