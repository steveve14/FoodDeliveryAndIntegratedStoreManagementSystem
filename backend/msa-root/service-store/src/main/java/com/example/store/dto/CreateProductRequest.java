package com.example.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** CreateProductRequest 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
  @NotBlank(message = "상품명을 입력해주세요.")
  private String name;

  private String description;

  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  private long price;
}
