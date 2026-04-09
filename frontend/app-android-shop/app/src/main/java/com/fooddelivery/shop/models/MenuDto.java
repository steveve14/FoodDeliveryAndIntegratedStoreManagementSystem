package com.fooddelivery.shop.models;

public class MenuDto {
    private String id;
    private String storeId;
    private String name;
    private String description;
    private int price;
    private boolean available;
    private String imageUrl;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getImageUrl() { return imageUrl; }
}
