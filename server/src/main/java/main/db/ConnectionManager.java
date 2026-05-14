package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final String url;
    private final String dbUser;
    private final String dbPassword;

    public ConnectionManager(String url, String username, String password) {
        this.url = url;
        this.dbUser = username;
        this.dbPassword = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

}
