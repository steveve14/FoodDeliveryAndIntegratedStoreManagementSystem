package com.fooddelivery.shop;

public class StoreManager {
    private static StoreManager instance;
    private String storeId;
    private String storeName;
    private String ownerName;
    private String status; // "OPEN" or "CLOSED"

    private StoreManager() {}

    public static StoreManager getInstance() {
        if (instance == null) instance = new StoreManager();
        return instance;
    }

    public void setStore(String id, String name, String ownerName) {
        this.storeId = id;
        this.storeName = name;
        this.ownerName = ownerName;
    }

    public String getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public String getOwnerName() { return ownerName; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void clear() {
        storeId = null;
        storeName = null;
        ownerName = null;
        status = null;
    }
}
