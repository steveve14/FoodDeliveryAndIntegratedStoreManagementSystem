package com.fooddelivery.shop.models;

public class UpdateStoreRequest {
    private String name;
    private String description;
    private String status;
    private String openingHours;
    private String category;
    private int minOrderAmount;

    public UpdateStoreRequest(String name, String description, String status,
                              String openingHours, String category, int minOrderAmount) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.openingHours = openingHours;
        this.category = category;
        this.minOrderAmount = minOrderAmount;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getOpeningHours() { return openingHours; }
    public String getCategory() { return category; }
    public int getMinOrderAmount() { return minOrderAmount; }
}
