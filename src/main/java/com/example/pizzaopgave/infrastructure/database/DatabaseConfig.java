package com.example.pizzaopgave.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    private static DatabaseConfig instance;

    private DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            // Default to MySQL database
            instance = new DatabaseConfig("jdbc:mysql://localhost:3306/pizzaparadise", "root", "root");
        }
        return instance;
    }

    public static void initialize(String url, String username, String password) {
        instance = new DatabaseConfig(url, username, password);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}