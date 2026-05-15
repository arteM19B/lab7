package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
        Properties properties = new Properties();
        properties.setProperty("user", dbUser);
        properties.setProperty("password", dbPassword);
        properties.setProperty("connectTimeout", "5");
        properties.setProperty("socketTimeout", "20");
        return DriverManager.getConnection(url, properties);
    }

}
