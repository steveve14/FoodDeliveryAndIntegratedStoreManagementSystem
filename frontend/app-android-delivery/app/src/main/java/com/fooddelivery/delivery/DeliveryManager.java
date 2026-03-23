package com.fooddelivery.delivery;

public class DeliveryManager {
    private static DeliveryManager instance;
    private String riderId;
    private String riderName;
    private String riderEmail;

    private DeliveryManager() {}

    public static DeliveryManager getInstance() {
        if (instance == null) instance = new DeliveryManager();
        return instance;
    }

    public void setRider(String id, String name, String email) {
        this.riderId = id;
        this.riderName = name;
        this.riderEmail = email;
    }

    public String getRiderId() { return riderId; }
    public String getRiderName() { return riderName; }
    public String getRiderEmail() { return riderEmail; }

    public void clear() {
        riderId = null;
        riderName = null;
        riderEmail = null;
    }
}
