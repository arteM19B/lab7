package main.db;

import main.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class UserDAO {
    private final ConnectionManager connectionManager;

    public UserDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Optional<User> findByLogin(String login) throws SQLException {
        String sql = """
                SELECT id, login, password_hash, created_at
                FROM users
                WHERE login = ?
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapUser(resultSet));
            }
        }
    }

    public User insert(String login, String passwordHash) throws SQLException {
        String sql = """
                INSERT INTO users (login, password_hash)
                VALUES (?, ?)
                RETURNING id, login, password_hash, created_at
                """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, passwordHash);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("User insert did not return a row");
                }
                return mapUser(resultSet);
            }
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }
}
