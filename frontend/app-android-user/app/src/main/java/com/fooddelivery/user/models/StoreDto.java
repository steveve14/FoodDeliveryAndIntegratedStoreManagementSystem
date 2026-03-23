package com.fooddelivery.user.models;

import java.util.List;

public class StoreDto {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String category;
    private String status;
    private double latitude;
    private double longitude;
    private int minOrderAmount;
    private double ratingAvg;
    private String description;
    private String openingHours;
    private String ownerId;
    private String eta;
    private int reviewCount;
    private String deliveryFee;
    private String heroIcon;
    private List<String> tags;
    private String bestseller;
    private String promo;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getMinOrderAmount() { return minOrderAmount; }
    public double getRatingAvg() { return ratingAvg; }
    public String getDescription() { return description; }
    public String getOpeningHours() { return openingHours; }
    public String getOwnerId() { return ownerId; }
    public String getEta() { return eta; }
    public int getReviewCount() { return reviewCount; }
    public String getDeliveryFee() { return deliveryFee; }
    public String getHeroIcon() { return heroIcon; }
    public List<String> getTags() { return tags; }
    public String getBestseller() { return bestseller; }
    public String getPromo() { return promo; }
}
