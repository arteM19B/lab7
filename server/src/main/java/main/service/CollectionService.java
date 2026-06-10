package main.service;

import collection.Location;
import collection.Route;
import main.db.CollectionDAO;
import main.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CollectionService {
    private final CollectionDAO collectionDAO;
    private final ConcurrentHashMap<Long, Route<Long>> collection = new ConcurrentHashMap<>();
    private final LocalDateTime initializationTime = LocalDateTime.now();

    public CollectionService(CollectionDAO collectionDAO) {
        this.collectionDAO = collectionDAO;
    }

    public void loadFromDatabase() throws SQLException {
        List<Route<Long>> routes = collectionDAO.findAll();
        collection.clear();
        for (Route<Long> route : routes) {
            collection.put(route.getId(), route);
        }
    }

    public Route<Long> add(Route<Long> route, User user) throws SQLException {
        Route<Long> inserted = collectionDAO.insert(route, user.getId());
        collection.put(inserted.getId(), inserted);
        return inserted;
    }

    public boolean update(long id, Route<Long> newRoute, User user) throws SQLException {
        boolean updated = collectionDAO.updateIfOwner(id, newRoute, user.getId());
        if (!updated) {
            return false;
        }

        Route<Long> oldRoute = collection.get(id);
        LocalDate creationDate = oldRoute == null ? newRoute.getCreationDate() : oldRoute.getCreationDate();
        Route<Long> routeWithId = new Route<>(
                id,
                newRoute.getName(),
                newRoute.getCoordinates(),
                newRoute.getFrom(),
                newRoute.getTo(),
                newRoute.getDistance(),
                creationDate
        );
        collection.put(id, routeWithId);
        return true;
    }

    public boolean removeById(long id, User user) throws SQLException {
        boolean deleted = collectionDAO.deleteIfOwner(id, user.getId());
        if (!deleted) {
            return false;
        }

        collection.remove(id);
        return true;
    }

    public int clearOwned(User user) throws SQLException {
        List<Long> removedIds = collectionDAO.deleteAllOwnedBy(user.getId());
        for (Long id : removedIds) {
            collection.remove(id);
        }
        return removedIds.size();
    }

    public boolean removeAt(int index, User user) throws SQLException {
        List<Route<Long>> snapshot = getCollectionSortedByLocation();
        if (index < 0 || index >= snapshot.size()) {
            return false;
        }

        return removeById(snapshot.get(index).getId(), user);
    }

    public boolean removeLast(User user) throws SQLException {
        List<Route<Long>> snapshot = getCollectionSortedByLocation();
        if (snapshot.isEmpty()) {
            return false;
        }

        return removeById(snapshot.get(snapshot.size() - 1).getId(), user);
    }

    public int removeAllByDistance(long distance, User user) throws SQLException {
        int removed = 0;
        for (Route<Long> route : snapshot()) {
            if (route.getDistance() == distance && removeById(route.getId(), user)) {
                removed++;
            }
        }
        return removed;
    }

    public int countGreaterThanDistance(long distance) {
        return (int) collection.values().stream()
                .filter(route -> route.getDistance() > distance)
                .count();
    }

    public String filterLessThanDistance(long distance) {
        List<Route<Long>> filtered = collection.values().stream()
                .filter(route -> route.getDistance() < distance)
                .sorted(locationComparator())
                .collect(Collectors.toList());

        String routes = filtered.stream()
                .map(Route::toString)
                .collect(Collectors.joining("\n"));

        return routes + (routes.isEmpty() ? "" : "\n") + "Shown " + filtered.size() + " elements";
    }

    public String show() {
        List<Route<Long>> routes = getCollectionSortedByLocation();
        if (routes.isEmpty()) {
            return "Collection is empty";
        }

        return routes.stream()
                .map(Route::toString)
                .collect(Collectors.joining("\n"));
    }

    public String info() {
        StringBuilder builder = new StringBuilder();
        builder.append("Collection type: ").append(collection.getClass().getSimpleName()).append("\n");
        builder.append("Initialization time: ").append(initializationTime).append("\n");
        builder.append("Elements count: ").append(collection.size()).append("\n");
        builder.append("Storage: PostgreSQL");
        return builder.toString();
    }

    public int size() {
        return collection.size();
    }

    public LocalDateTime getInitializationTime() {
        return initializationTime;
    }

    public List<Route<Long>> snapshot() {
        return new ArrayList<>(collection.values());
    }

    public List<Route<Long>> getCollectionSortedByLocation() {
        return collection.values().stream()
                .sorted(locationComparator())
                .collect(Collectors.toList());
    }

    public Route<Long> getById(long id) {
        return collection.get(id);
    }

    private Comparator<Route<Long>> locationComparator() {
        return Comparator.comparing((Route<Long> route) -> locationKey(route.getFrom()))
                .thenComparing(route -> locationKey(route.getTo()))
                .thenComparing(Route::getName);
    }

    private String locationKey(Location location) {
        return location == null ? "" : location.toString();
    }
}
