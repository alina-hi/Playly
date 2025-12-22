package com.playly;

import java.sql.*;
import java.util.*;

public class PlaygroundDAO {

    // Получить все площадки
    public List<Playground> getAllPlaygrounds() throws SQLException {
        List<Playground> playgrounds = new ArrayList<>();

        // SQL запрос с JOIN для получения типа локации
        /*String sql = "SELECT p.*, lt.name as type_name FROM playgrounds p " +
                "LEFT JOIN location_types lt ON p.location_type_id = lt.id " +
                "ORDER BY p.id";*/

        // Убрали JOIN с location_types, берем только location_type_id
        String sql = "SELECT p.* FROM playgrounds p ORDER BY p.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Playground pg = mapResultSetToPlayground(rs);
                pg.setAmenities(getAmenitiesForPlayground(conn, pg.getId()));
                playgrounds.add(pg);
            }
        }
        return playgrounds;
    }

    // Фильтрация по аттракционам
    public List<Playground> filterByAmenities(List<String> amenityFilters) throws SQLException {
        if (amenityFilters == null || amenityFilters.isEmpty()) {
            return getAllPlaygrounds();
        }

        List<Playground> result = new ArrayList<>();

        // Создаем строку с вопросительными знаками для подготовленного выражения
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < amenityFilters.size(); i++) {
            placeholders.append("?");
            if (i < amenityFilters.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT DISTINCT p.*, lt.name as type_name FROM playgrounds p " +
                "LEFT JOIN location_types lt ON p.location_type_id = lt.id " +
                "JOIN playground_amenities pa ON p.id = pa.playground_id " +
                "JOIN amenities a ON pa.amenity_id = a.id " +
                "WHERE a.name IN (" + placeholders + ") " +
                "ORDER BY p.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Устанавливаем параметры
            for (int i = 0; i < amenityFilters.size(); i++) {
                pstmt.setString(i + 1, amenityFilters.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Playground pg = mapResultSetToPlayground(rs);
                pg.setAmenities(getAmenitiesForPlayground(conn, pg.getId()));
                result.add(pg);
            }
        }
        return result;
    }

    // Получить площадку по ID
    public Playground getById(int id) throws SQLException {
        /*String sql = "SELECT p.*, lt.name as type_name FROM playgrounds p " +
                "LEFT JOIN location_types lt ON p.location_type_id = lt.id " +
                "WHERE p.id = ?";*/
        String sql = "SELECT p.* FROM playgrounds p WHERE p.id = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Playground pg = mapResultSetToPlayground(rs);
                pg.setAmenities(getAmenitiesForPlayground(conn, id));
                return pg;
            }
        }
        return null;
    }

    // Маппинг ResultSet в объект Playground
    // Маппинг ResultSet в объект Playground
    private Playground mapResultSetToPlayground(ResultSet rs) throws SQLException {
        // Проверяем наличие новых полей в ResultSet
        boolean hasNewFields = false;
        try {
            rs.findColumn("surface_type");
            hasNewFields = true;
        } catch (SQLException e) {
            hasNewFields = false;
        }

        if (hasNewFields) {
            // Создаем с новыми полями
            return new Playground(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getInt("location_type_id"),
                    rs.getString("description"),
                    rs.getObject("avg_safety_rating", Float.class),
                    rs.getBoolean("has_parking"),
                    rs.getBoolean("has_toilet"),
                    rs.getBoolean("has_video_surveillance"),
                    rs.getTimestamp("created_at"),
                    rs.getString("surface_type"),
                    rs.getString("age_category"),
                    rs.getBoolean("is_illuminated"),
                    rs.getBoolean("is_fenced"),
                    rs.getBoolean("has_shade")
            );
        } else {
            // Создаем без новых полей - используем значения по умолчанию
            return new Playground(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getInt("location_type_id"),
                    rs.getString("description"),
                    rs.getObject("avg_safety_rating", Float.class),
                    rs.getBoolean("has_parking"),
                    rs.getBoolean("has_toilet"),
                    rs.getBoolean("has_video_surveillance"),
                    new java.util.Date(), // текущая дата по умолчанию
                    "не указано",        // surface_type по умолчанию
                    "все возраста",      // age_category по умолчанию
                    false,               // is_illuminated по умолчанию
                    false,               // is_fenced по умолчанию
                    false                // has_shade по умолчанию
            );
        }
    }

    // Получить список аттракционов для площадки
    private List<String> getAmenitiesForPlayground(Connection conn, int playgroundId) throws SQLException {
        List<String> amenities = new ArrayList<>();
        String sql = "SELECT a.name FROM amenities a " +
                "JOIN playground_amenities pa ON a.id = pa.amenity_id " +
                "WHERE pa.playground_id = ? " +
                "ORDER BY a.name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playgroundId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                amenities.add(rs.getString("name"));
            }
        }
        return amenities;
    }

    // Получить все доступные аттракционы (для фильтров на фронтенде)
    public List<String> getAllAmenities() throws SQLException {
        List<String> amenities = new ArrayList<>();
        String sql = "SELECT name FROM amenities ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                amenities.add(rs.getString("name"));
            }
        }
        return amenities;
    }
}