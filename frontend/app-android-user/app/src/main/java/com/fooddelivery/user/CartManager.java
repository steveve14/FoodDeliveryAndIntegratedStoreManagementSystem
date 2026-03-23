package com.fooddelivery.user;

import com.fooddelivery.user.models.CreateOrderRequest.OrderItemRequest;
import com.fooddelivery.user.models.MenuDto;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private String storeId;
    private String storeName;
    private final List<CartItem> items = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void addItem(MenuDto menu, String storeId, String storeName) {
        if (this.storeId != null && !this.storeId.equals(storeId)) {
            items.clear();
        }
        this.storeId = storeId;
        this.storeName = storeName;
        
        for (CartItem item : items) {
            if (item.menu.getId().equals(menu.getId())) {
                item.quantity++;
                return;
            }
        }
        items.add(new CartItem(menu, 1));
    }

    public List<CartItem> getItems() { return items; }
    public String getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    
    public int getTotalAmount() {
        int total = 0;
        for (CartItem item : items) {
            total += item.menu.getPrice() * item.quantity;
        }
        return total;
    }

    public void clear() {
        items.clear();
        storeId = null;
        storeName = null;
    }

    public static class CartItem {
        public MenuDto menu;
        public int quantity;

        public CartItem(MenuDto menu, int quantity) {
            this.menu = menu;
            this.quantity = quantity;
        }
    }
}
