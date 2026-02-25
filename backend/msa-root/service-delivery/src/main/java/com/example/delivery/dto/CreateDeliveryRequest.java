package com.example.delivery.dto;

public record CreateDeliveryRequest(Long orderId, String address, String courier) {
}
