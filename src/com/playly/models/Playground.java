package com.playly.models;

import java.util.Date;
import java.util.List;

public class Playground {
    private int id;
    private String name;
    private String address;
    private int locationTypeId;
    private String description;
    private Float avgSafetyRating;
    private boolean hasParking;
    private boolean hasToilet;
    private boolean hasVideoSurveillance;
    private Date createdAt;
    private String surfaceType;
    private String ageCategory;
    private boolean isIlluminated;
    private boolean isFenced;
    private boolean hasShade;
    private List<String> amenities;

    // Конструктор
    public Playground(int id, String name, String address, int locationTypeId,
                      String description, Float avgSafetyRating, boolean hasParking,
                      boolean hasToilet, boolean hasVideoSurveillance, Date createdAt,
                      String surfaceType, String ageCategory, boolean isIlluminated,
                      boolean isFenced, boolean hasShade) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationTypeId = locationTypeId;
        this.description = description;
        this.avgSafetyRating = avgSafetyRating;
        this.hasParking = hasParking;
        this.hasToilet = hasToilet;
        this.hasVideoSurveillance = hasVideoSurveillance;
        this.createdAt = createdAt;
        this.surfaceType = surfaceType;
        this.ageCategory = ageCategory;
        this.isIlluminated = isIlluminated;
        this.isFenced = isFenced;
        this.hasShade = hasShade;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getLocationTypeId() { return locationTypeId; }
    public String getDescription() { return description; }
    public Float getAvgSafetyRating() { return avgSafetyRating; }
    public boolean isHasParking() { return hasParking; }
    public boolean isHasToilet() { return hasToilet; }
    public boolean isHasVideoSurveillance() { return hasVideoSurveillance; }
    public Date getCreatedAt() { return createdAt; }
    public String getSurfaceType() { return surfaceType; }
    public String getAgeCategory() { return ageCategory; }
    public boolean isIlluminated() { return isIlluminated; }
    public boolean isFenced() { return isFenced; }
    public boolean isHasShade() { return hasShade; }
    public List<String> getAmenities() { return amenities; }

    // Сеттер для amenities
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    // Для отладки
    @Override
    public String toString() {
        return "Playground{id=" + id + ", name='" + name + "', address='" + address + "'}";
    }
}