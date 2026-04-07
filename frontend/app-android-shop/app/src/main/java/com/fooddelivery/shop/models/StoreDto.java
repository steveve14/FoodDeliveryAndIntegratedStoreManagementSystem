package com.fooddelivery.shop.models;

public class StoreDto {
    private String id;
    private String name;
    private String ownerId;
    private String status;
    private String openingHours;
    private double ratingAvg;
    private int reviewCount;
    private String category;
    private int minOrderAmount;
    private String description;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerId() { return ownerId; }
    public String getStatus() { return status; }
    public String getOpeningHours() { return openingHours; }
    public double getRatingAvg() { return ratingAvg; }
    public int getReviewCount() { return reviewCount; }
    public String getCategory() { return category; }
    public int getMinOrderAmount() { return minOrderAmount; }
    public String getDescription() { return description; }
}
