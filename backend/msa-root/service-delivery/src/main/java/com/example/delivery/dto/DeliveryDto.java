package com.example.delivery.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

  private String id;
  private String orderId;
  private String courier;
  private String status;
  private int deliveryFee;
  private Instant scheduledAt;
}
