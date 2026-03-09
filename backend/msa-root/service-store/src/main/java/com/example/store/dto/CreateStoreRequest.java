package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {

  @NotBlank(message = "가게 이름을 입력해주세요.")
  private String name;

  @NotBlank(message = "주소를 입력해주세요.")
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
}
