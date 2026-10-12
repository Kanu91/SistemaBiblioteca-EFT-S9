package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Singleton de configuración: entrega una conexión NUEVA por operación para permitir concurrencia. */
public final class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final String url;
    private final String user;

    private DatabaseConnection() {
        String host = envOr("MYSQL_HOST", "127.0.0.1");
        String port = envOr("MYSQL_PORT", "3306");
        String database = envOr("MYSQL_DATABASE", "biblioteca");
        url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=UTF-8&connectionTimeZone=LOCAL";
        user = envOr("MYSQL_USER", "root");
    }

    public static DatabaseConnection getInstance() {
        DatabaseConnection local = instance;
        if (local == null) {
            synchronized (DatabaseConnection.class) {
                local = instance;
                if (local == null) instance = local = new DatabaseConnection();
            }
        }
        return local;
    }

    public Connection getConnection() throws SQLException {
        String password = System.getenv("MYSQL_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new SQLException("Falta la variable de entorno MYSQL_PASSWORD en la configuración de ejecución.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String envOr(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
