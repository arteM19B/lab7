package main.db;

import collection.Coordinates;
import collection.Location;
import collection.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {
    private final ConnectionManager connectionManager;

    public CollectionDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<Route<Long>> findAll() throws SQLException {
        String sql = """
                SELECT id, name, coordinate_x, coordinate_y,
                       from_name, from_x, from_y,
                       to_name, to_x, to_y,
                       distance, creation_date, owner_id
                FROM roads
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Route<Long>> routes = new ArrayList<>();
            while (resultSet.next()) {
                routes.add(mapRoute(resultSet));
            }
            return routes;
        }
    }

    public Route<Long> insert(Route<Long> route, long ownerId) throws SQLException {
        String sql = """
                INSERT INTO roads (
                    name, coordinate_x, coordinate_y,
                    from_name, from_x, from_y,
                    to_name, to_x, to_y,
                    distance, owner_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id, name, coordinate_x, coordinate_y,
                          from_name, from_x, from_y,
                          to_name, to_x, to_y,
                          distance, creation_date, owner_id
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillRouteFields(statement, route);
            statement.setLong(11, ownerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Route insert did not return a row");
                }
                return mapRoute(resultSet);
            }
        }
    }

    public boolean updateIfOwner(long id, Route<Long> route, long ownerId) throws SQLException {
        String sql = """
                UPDATE roads
                SET name = ?,
                    coordinate_x = ?,
                    coordinate_y = ?,
                    from_name = ?,
                    from_x = ?,
                    from_y = ?,
                    to_name = ?,
                    to_x = ?,
                    to_y = ?,
                    distance = ?
                WHERE id = ?
                  AND owner_id = ?
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillRouteFields(statement, route);
            statement.setLong(11, id);
            statement.setLong(12, ownerId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteIfOwner(long id, long ownerId) throws SQLException {
        String sql = """
                DELETE FROM roads
                WHERE id = ?
                  AND owner_id = ?
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, ownerId);
            return statement.executeUpdate() == 1;
        }
    }

    public List<Long> deleteAllOwnedBy(long ownerId) throws SQLException {
        String sql = """
                DELETE FROM roads
                WHERE owner_id = ?
                RETURNING id
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ownerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Long> removedIds = new ArrayList<>();
                while (resultSet.next()) {
                    removedIds.add(resultSet.getLong("id"));
                }
                return removedIds;
            }
        }
    }

    private void fillRouteFields(PreparedStatement statement, Route<Long> route) throws SQLException {
        statement.setString(1, route.getName());
        statement.setLong(2, route.getCoordinates().getX());
        statement.setInt(3, route.getCoordinates().getY());
        setLocation(statement, 4, route.getFrom());
        setLocation(statement, 7, route.getTo());
        statement.setLong(10, route.getDistance());
    }

    private void setLocation(PreparedStatement statement, int startIndex, Location location) throws SQLException {
        if (location == null) {
            statement.setNull(startIndex, Types.VARCHAR);
            statement.setNull(startIndex + 1, Types.DOUBLE);
            statement.setNull(startIndex + 2, Types.DOUBLE);
            return;
        }

        statement.setString(startIndex, location.getName());
        statement.setDouble(startIndex + 1, location.getX());
        statement.setDouble(startIndex + 2, location.getY());
    }

    private Route<Long> mapRoute(ResultSet resultSet) throws SQLException {
        Coordinates coordinates = new Coordinates(
                resultSet.getLong("coordinate_x"),
                resultSet.getInt("coordinate_y")
        );

        return new Route<>(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                coordinates,
                mapLocation(resultSet, "from"),
                mapLocation(resultSet, "to"),
                resultSet.getLong("distance"),
                getCreationDate(resultSet)
        );
    }

    private Location mapLocation(ResultSet resultSet, String prefix) throws SQLException {
        Double x = getNullableDouble(resultSet, prefix + "_x");
        Double y = getNullableDouble(resultSet, prefix + "_y");
        String name = resultSet.getString(prefix + "_name");

        if (x == null && y == null && name == null) {
            return null;
        }
        if (x == null || y == null) {
            throw new SQLException("Invalid nullable location columns for " + prefix);
        }

        return new Location(x.floatValue(), y, name);
    }

    private Double getNullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }

    private LocalDate getCreationDate(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("creation_date");
        return timestamp == null ? LocalDate.now() : timestamp.toLocalDateTime().toLocalDate();
    }
}
