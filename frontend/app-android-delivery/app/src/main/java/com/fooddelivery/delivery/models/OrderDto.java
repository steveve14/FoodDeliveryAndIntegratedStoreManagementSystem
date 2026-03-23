package com.fooddelivery.delivery.models;

import java.util.List;

public class OrderDto {
    private String id;
    private String userId;
    private String storeId;
    private int totalAmount;
    private List<OrderItemDto> items;
    private String status;
    private String createdAt;
    private String address;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getStoreId() { return storeId; }
    public int getTotalAmount() { return totalAmount; }
    public List<OrderItemDto> getItems() { return items; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getAddress() { return address; }
}
