package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AddressDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

  private String id;
  private String userId;
  private String label;
  private String line1;
  private String line2;
  private String city;
  private String state;
  private String postalCode;
  private String country;
  private boolean primaryAddress;
  private Instant createdAt;
}
