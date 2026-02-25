package com.example.order.dto;

/**
 * 주문 항목 DTO
 */
public record OrderItemDto(Long productId, int quantity, long price) {
}
