package main.db;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

    public static String getDatabaseErrorMessage(SQLException exception) {
        if (hasCause(exception, ConnectException.class)) {
            return "Database is unavailable: check host, port and that PostgreSQL is running";
        }

        if (hasCause(exception, SocketTimeoutException.class)) {
            return "Database connection timed out";
        }

        if (hasCause(exception, UnknownHostException.class)) {
            return "Database host was not found";
        }

        String sqlState = exception.getSQLState();
        if (sqlState == null) {
            return "Database error: " + exception.getMessage();
        }

        if (sqlState.startsWith("08")) {
            return "Database connection error";
        }
        if ("28P01".equals(sqlState)) {
            return "Database authorization failed: check login and password";
        }
        if ("3D000".equals(sqlState)) {
            return "Database does not exist";
        }
        if ("42P01".equals(sqlState)) {
            return "Database table was not found";
        }
        if ("23505".equals(sqlState)) {
            return "Database already contains this value";
        }

        return "Database error: " + exception.getMessage();
    }

    public static String getDatabaseLogMessage(SQLException exception) {
        StringBuilder builder = new StringBuilder(getDatabaseErrorMessage(exception));
        if (exception.getSQLState() != null) {
            builder.append(" (SQLState: ").append(exception.getSQLState()).append(")");
        }
        if (exception.getErrorCode() != 0) {
            builder.append(" (error code: ").append(exception.getErrorCode()).append(")");
        }
        return builder.toString();
    }

    private static boolean hasCause(Throwable throwable, Class<? extends Throwable> causeType) {
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
