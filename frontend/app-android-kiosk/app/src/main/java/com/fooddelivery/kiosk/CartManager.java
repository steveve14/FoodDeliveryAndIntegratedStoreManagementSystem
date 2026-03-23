package com.fooddelivery.kiosk;

import com.fooddelivery.kiosk.models.MenuDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartManager {
    private static CartManager instance;
    private final Map<String, CartItem> cartItems = new HashMap<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void addItem(MenuDto menu) {
        if (cartItems.containsKey(menu.getId())) {
            Objects.requireNonNull(cartItems.get(menu.getId())).quantity++;
        } else {
            cartItems.put(menu.getId(), new CartItem(menu, 1));
        }
    }

    public void removeItem(String menuId) {
        if (cartItems.containsKey(menuId)) {
            CartItem item = cartItems.get(menuId);
            assert item != null;
            if (item.quantity > 1) {
                item.quantity--;
            } else {
                cartItems.remove(menuId);
            }
        }
    }

    public Map<String, CartItem> getCartItems() {
        return cartItems;
    }

    public int getTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems.values()) {
            total += (item.menu.getPrice() * item.quantity);
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
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
