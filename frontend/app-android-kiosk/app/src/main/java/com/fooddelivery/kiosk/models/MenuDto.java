package com.fooddelivery.kiosk.models;

public class MenuDto {
    private String id;
    private String storeId;
    private String name;
    private String description;
    private int price;
    private String category;
    private String imageUrl;
    private boolean isAvailable;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isAvailable() { return isAvailable; }
}
