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

    // Конструкторы и геттеры/сеттеры
    public User() {}

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isVerified = false;
        this.reputation = 0.0;
        this.isActive = true;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    // В класс User добавьте этот метод (после метода addToReputation):

    // Метод для получения статуса в виде строки
    public String getStatusString() {
        return isVerified ? "Верифицированный родитель" : "Непроверенный родитель";
    }
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

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', verified=%s, reputation=%.2f}",
                id, username, email, isVerified ? "✓" : "✗", reputation);
    }
}