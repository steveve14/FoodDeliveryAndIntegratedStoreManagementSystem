package com.fooddelivery.user.models;

import java.util.List;

public class CreateOrderRequest {
    private String address;
    private int totalAmount;
    private List<OrderItemRequest> items;

    public CreateOrderRequest(String address, int totalAmount, List<OrderItemRequest> items) {
        this.address = address;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getAddress() { return address; }
    public int getTotalAmount() { return totalAmount; }
    public List<OrderItemRequest> getItems() { return items; }
    
    public static class OrderItemRequest {
        private String productId;
        private int quantity;
        private int price;

        public OrderItemRequest(String productId, int quantity, int price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public int getPrice() { return price; }
    }
}
