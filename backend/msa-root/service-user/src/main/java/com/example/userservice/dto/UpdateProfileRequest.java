package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** UpdateProfileRequest 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
  private String name;
  private String username;
  private String phone;
  private String avatarUrl;
  private String location;
}
