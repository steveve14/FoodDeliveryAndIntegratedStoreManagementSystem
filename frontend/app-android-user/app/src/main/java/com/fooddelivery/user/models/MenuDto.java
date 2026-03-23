package com.fooddelivery.user.models;

public class MenuDto {
    private String id;
    private String storeId;
    private String name;
    private String description;
    private int price;
    private boolean available;
    private String createdAt;

    public String getId() { return id; }
    public String getStoreId() { return storeId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getCreatedAt() { return createdAt; }
}
