package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** DTO for updating a user's profile information. */
/** UpdateProfileRequest 타입입니다. */
public class UpdateProfileRequest {
  private String name;
  private String username;
  private String phone;
  private String avatarUrl;
  private String location;
}
