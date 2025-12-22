package com.playly.dao;

import com.playly.DatabaseConnection;
import com.playly.models.User;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Optional;

public class UserDAO {

    public UserDAO() {
        // Конструктор не нуждается в инициализации
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (email, username, password_hash, is_verified, reputation) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setBoolean(4, user.isVerified());
            stmt.setDouble(5, user.getReputation());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_active = TRUE";
        return getUserByField(sql, email);
    }

    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        return getUserByField(sql, username);
    }

    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ? AND is_active = TRUE";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email).isPresent();
    }

    public boolean usernameExists(String username) {
        return getUserByUsername(username).isPresent();
    }

    // Метод для проверки пароля при входе
    public boolean checkPassword(String email, String password) {
        try {
            Optional<User> userOpt = getUserByEmail(email);
            if (!userOpt.isPresent()) {
                return false;
            }

            User user = userOpt.get();

            // Хешируем введенный пароль для сравнения
            String inputHash = hashPassword(password);
            String storedHash = user.getPasswordHash();

            // Сравниваем хеши
            return inputHash.equals(storedHash);

        } catch (Exception e) {
            System.err.println("Error checking password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для проверки пароля по username
    public boolean checkPasswordByUsername(String username, String password) {
        try {
            Optional<User> userOpt = getUserByUsername(username);
            if (!userOpt.isPresent()) {
                return false;
            }

            User user = userOpt.get();
            String inputHash = hashPassword(password);
            return inputHash.equals(user.getPasswordHash());

        } catch (Exception e) {
            System.err.println("Error checking password by username: " + e.getMessage());
            return false;
        }
    }

    // Метод для обновления репутации
    public boolean updateReputation(int userId, double newReputation) {
        String sql = "UPDATE users SET reputation = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ограничиваем диапазон
            double limitedReputation = Math.max(-10.0, Math.min(10.0, newReputation));
            stmt.setDouble(1, limitedReputation);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating reputation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Метод для верификации пользователя
    public boolean verifyUser(int userId) {
        String sql = "UPDATE users SET is_verified = TRUE WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error verifying user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Метод для "мягкого" удаления пользователя
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Метод для изменения пароля
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String newHash = hashPassword(newPassword);
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Optional<User> getUserByField(String sql, String value) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setReputation(rs.getDouble("reputation"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }

        user.setActive(rs.getBoolean("is_active"));
        return user;
    }

    // Метод для хеширования пароля (используется в RegistrationServlet и здесь)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            // Fallback на простой хеш
            System.err.println("SHA-256 hashing failed, using simple hash: " + e.getMessage());
            return Integer.toHexString(password.hashCode());
        }
    }

    // Вспомогательный метод для тестирования хеширования
    public String hashPasswordForTest(String password) {
        return hashPassword(password);
    }
}