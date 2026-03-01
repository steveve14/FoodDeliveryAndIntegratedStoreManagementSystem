package com.example.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequest {
  @NotBlank(message = "주문 ID를 입력해주세요.")
  private String orderId;

  private String address;
  private String courier;
}
