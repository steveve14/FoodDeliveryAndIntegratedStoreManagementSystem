package com.example.order.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FrontendCustomerOrderSummaryDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendCustomerOrderSummaryDto {
  private String userId;
  private long ordersCount;
  private Instant lastOrderAt;
  private String grade;
}
