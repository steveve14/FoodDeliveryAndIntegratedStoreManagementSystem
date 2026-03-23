package com.fooddelivery.shop.models;

public class OrderStatusRequest {
    private String status;

    public OrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
}
