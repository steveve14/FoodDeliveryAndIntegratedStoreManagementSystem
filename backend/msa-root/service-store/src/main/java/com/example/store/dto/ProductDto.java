package com.example.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** ProductDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

  private String id;
  private String storeId;
  private String name;
  private String description;
  private long price;
}
