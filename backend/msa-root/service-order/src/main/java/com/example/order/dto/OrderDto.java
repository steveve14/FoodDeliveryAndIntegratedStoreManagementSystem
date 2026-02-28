package com.example.order.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

  private String id;
  private String userId;
  private String storeId;
  private int totalAmount;
  private List<OrderItemDto> items;
  private String status;
  private Instant createdAt;
}
