package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** UserProfileDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

  private String id;
  private String email;
  private String name;
  private String username;
  private String phone;
  private String avatarUrl;
  private String location;
}
