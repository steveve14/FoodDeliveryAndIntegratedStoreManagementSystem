package com.example.store.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {

  private String id;
  private String name;
  private String address;
  private String phone;
  private String category;
  private String status;
  private Double latitude;
  private Double longitude;
  private Long minOrderAmount;
  private Double ratingAvg;
  private String description;
  private String openingHours;
  private String ownerId;
  private String eta;
  private Integer reviewCount;
  private String deliveryFee;
  private String heroIcon;
  private List<String> tags;
  private String bestseller;
  private String promo;
}
