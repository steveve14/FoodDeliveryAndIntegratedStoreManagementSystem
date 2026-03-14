package com.example.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** OrderItemDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

  @NotBlank(message = "상품 ID를 입력해주세요.")
  private String productId;

  @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
  private int quantity;

  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  private long price;
}
