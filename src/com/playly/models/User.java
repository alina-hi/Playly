package com.playly.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String email;
    private String username;
    private String passwordHash;
    private boolean isVerified;
    private double reputation;
    private LocalDateTime createdAt;
    private boolean isActive;

    // НОВЫЕ ПОЛЯ ДЛЯ ПРОФИЛЯ:
    private String displayName;      // Отображаемое имя
    private String about;           // О себе
    private String childAgeRange;   // Возраст детей: "3-5", "6-10", etc.
    private String interests;       // Интересы (JSON или текст)
    private String location;        // Район/город
    private String avatarUrl;       // Ссылка на аватар

    // Конструкторы
    public User() {}

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isVerified = false;
        this.reputation = 0.0;
        this.isActive = true;
        this.displayName = username; // По умолчанию как username
    }

    // Геттеры и сеттеры (старые + новые)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public double getReputation() { return reputation; }
    public void setReputation(double reputation) {
        if (reputation < -10.0) this.reputation = -10.0;
        else if (reputation > 10.0) this.reputation = 10.0;
        else this.reputation = reputation;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Новые геттеры/сеттеры
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getChildAgeRange() { return childAgeRange; }
    public void setChildAgeRange(String childAgeRange) { this.childAgeRange = childAgeRange; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    // Метод для получения статуса в виде строки (УБЕДИТЕСЬ ЧТО ОН ТОЛЬКО ОДИН!)
    public String getStatusString() {
        return isVerified ? "Верифицированный родитель" : "Непроверенный родитель";
    }

    // Метод для увеличения/уменьшения репутации
    public void addToReputation(double value) {
        setReputation(this.reputation + value);
    }

    // Метод для получения информации о детях (обобщенной)
    public String getChildInfo() {
        if (childAgeRange == null || childAgeRange.isEmpty()) {
            return "Не указано";
        }
        return "Дети: " + childAgeRange + " лет";
    }

    // Метод для получения интересов как списка
    public String[] getInterestsList() {
        if (interests == null || interests.isEmpty()) {
            return new String[0];
        }
        return interests.split(",");
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%d, username='%s', displayName='%s', verified=%s, reputation=%.2f, location='%s'}",
                id, username, displayName, isVerified ? "✓" : "✗", reputation, location
        );
    }
}