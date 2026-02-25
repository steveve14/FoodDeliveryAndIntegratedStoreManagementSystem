package com.example.order.dto;

import java.time.Instant;
import java.util.List;

/**
 * 주문 정보를 표현하는 DTO
 */
public record OrderDto(Long id, Long userId, List<OrderItemDto> items, String status, Instant createdAt) {
}
