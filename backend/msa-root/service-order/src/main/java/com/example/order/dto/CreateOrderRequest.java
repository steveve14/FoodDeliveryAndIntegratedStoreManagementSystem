package com.example.order.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
	
  private String userId;
  private List<OrderItemDto> items;
  private String address;
  private long totalAmount;
}
