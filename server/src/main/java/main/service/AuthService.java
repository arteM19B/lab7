package main.service;

import main.db.UserDAO;
import main.model.User;
import main.util.PasswordHasher;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String login, String rawPassword) throws SQLException {
        validateCredential(login, rawPassword);

        String normalizedLogin = login.trim();
        String hash = PasswordHasher.sha384(rawPassword);

        try {
            return userDAO.insert(normalizedLogin, hash);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("User already exists");
            }
            throw e;
        }
    }

    public Optional<User> authenticate(String login, String rawPassword) throws SQLException {
        if (login == null || rawPassword == null) {
            return Optional.empty();
        }

        String normalizedLogin = login.trim();
        String hash = PasswordHasher.sha384(rawPassword);

        Optional<User> user = userDAO.findByLogin(normalizedLogin);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (!user.get().getPasswordHash().equals(hash)) {
            return Optional.empty();
        }

        return user;
    }

    private void validateCredential(String login, String rawPassword) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login cannot be empty");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
