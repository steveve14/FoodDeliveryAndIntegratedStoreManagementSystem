package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FavoriteStoreDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStoreDto {
  private String id;
  private String userId;
  private String storeId;
  private String name;
  private String category;
  private double rating;
  private String deliveryTime;
  private String minOrder;
  private String imageIcon;
  private Instant createdAt;
}
