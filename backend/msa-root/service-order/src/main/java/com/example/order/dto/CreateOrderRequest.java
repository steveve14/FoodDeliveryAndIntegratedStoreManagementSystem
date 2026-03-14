package com.example.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** CreateOrderRequest 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

  private String userId;

  @NotEmpty(message = "주문 항목을 입력해주세요.")
  @Valid
  private List<OrderItemDto> items;

  @NotBlank(message = "주소를 입력해주세요.")
  private String address;

  @Min(value = 0, message = "총 금액은 0 이상이어야 합니다.")
  private long totalAmount;
}
