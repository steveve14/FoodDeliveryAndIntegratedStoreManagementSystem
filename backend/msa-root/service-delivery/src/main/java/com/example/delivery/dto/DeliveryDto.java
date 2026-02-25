package com.example.delivery.dto;

import java.time.Instant;

public record DeliveryDto(Long id, Long orderId, String courier, String status, Instant scheduledAt) {
}
