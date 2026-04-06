package com.fooddelivery.delivery.models;

import com.google.gson.annotations.SerializedName;

public class DeliveryDto {
    @SerializedName("id")
    private String id;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("courier")
    private String courier;

    @SerializedName("status")
    private String status;

    @SerializedName("deliveryFee")
    private int deliveryFee;

    @SerializedName("address")
    private String address;

    @SerializedName("scheduledAt")
    private String scheduledAt;

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCourier() { return courier; }
    public String getStatus() { return status; }
    public int getDeliveryFee() { return deliveryFee; }
    public String getAddress() { return address; }
    public String getScheduledAt() { return scheduledAt; }
}
