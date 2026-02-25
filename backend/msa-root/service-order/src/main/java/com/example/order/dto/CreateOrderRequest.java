package com.example.order.dto;

import java.util.List;

/**
 * 주문 생성 요청 DTO
 */
public record CreateOrderRequest(Long userId, List<OrderItemDto> items, String address) {
}
