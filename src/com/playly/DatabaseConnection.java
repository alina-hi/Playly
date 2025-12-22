package com.playly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    private static final String URL = "jdbc:mysql://localhost:3306/playly_spb";
    private static final String USER = "root"; // ваш пользователь БД
    private static final String PASSWORD = "ikpi35"; // ваш пароль

    static {
        try {
            // Регистрируем драйвер MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver registered successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Cannot load MySQL driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful to: " + URL);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection FAILED!");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    // Тестовый метод для проверки подключения
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connection test PASSED");
        } catch (SQLException e) {
            System.out.println("❌ Database connection test FAILED: " + e.getMessage());
        }
    }

}