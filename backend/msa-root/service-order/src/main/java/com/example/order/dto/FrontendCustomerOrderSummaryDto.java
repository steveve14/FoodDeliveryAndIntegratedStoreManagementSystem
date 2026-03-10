package com.example.order.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendCustomerOrderSummaryDto {
  private String userId;
  private long ordersCount;
  private Instant lastOrderAt;
  private String grade;
}