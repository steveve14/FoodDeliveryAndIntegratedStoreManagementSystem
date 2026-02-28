package com.example.store.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    
  private String id;
  private String storeId;
  private String name;
  private String description;
  private long price;
  private boolean available;
  private Instant createdAt;
}
