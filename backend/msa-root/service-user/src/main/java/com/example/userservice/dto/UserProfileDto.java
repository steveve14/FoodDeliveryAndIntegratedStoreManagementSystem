package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
 * DTO representing a user's public profile information.
 */
public class UserProfileDto {

  private String id;
  private String email;
  private String name;
  private String phone;
}
