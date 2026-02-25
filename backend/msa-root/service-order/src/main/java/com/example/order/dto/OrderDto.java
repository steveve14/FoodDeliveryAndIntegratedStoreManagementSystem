package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	private Long id;
	private Long userId;
	private List<OrderItemDto> items;
	private String status;
	private Instant createdAt;
}
