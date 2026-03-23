package com.fooddelivery.user.models;

public class OrderItemDto {
    private String productId;
    private int quantity;
    private int price;

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
}
