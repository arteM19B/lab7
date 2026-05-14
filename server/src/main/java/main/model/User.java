package main.model;

import java.time.LocalDateTime;

public class User {
    private final long id;
    private final String login;
    private final String passwordHash;
    private final LocalDateTime createdAt;

    public User(long id, String login, String passwordHash) {
        this(id, login, passwordHash, null);
    }

    public User(long id, String login, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
