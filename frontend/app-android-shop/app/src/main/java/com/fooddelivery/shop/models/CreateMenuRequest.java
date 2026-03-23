package com.fooddelivery.shop.models;

public class CreateMenuRequest {
    private String name;
    private String description;
    private int price;
    private boolean available;

    public CreateMenuRequest(String name, String description, int price, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }
}
