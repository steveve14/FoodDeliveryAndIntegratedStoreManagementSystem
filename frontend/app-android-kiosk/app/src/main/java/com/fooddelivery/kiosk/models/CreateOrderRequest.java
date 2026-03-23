package com.fooddelivery.kiosk.models;

import java.util.List;

public class CreateOrderRequest {
    private String storeId;
    private List<Item> items;
    private String paymentMethod;
    private String address; // Optional for kiosk

    public CreateOrderRequest(String storeId, List<Item> items, String paymentMethod, String address) {
        this.storeId = storeId;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.address = address;
    }

    public static class Item {
        private String productId;
        private int quantity;

        public Item(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
