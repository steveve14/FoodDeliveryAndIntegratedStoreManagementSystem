package com.fooddelivery.shop.models;

public class ReviewDto {
    private String id;
    private String orderId;
    private String userId;
    private String userName;
    private String storeId;
    private int rating;
    private String content;
    private String reply;
    private String createdAt;

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getStoreId() { return storeId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public String getReply() { return reply; }
    public String getCreatedAt() { return createdAt; }

    public void setReply(String reply) { this.reply = reply; }
}
