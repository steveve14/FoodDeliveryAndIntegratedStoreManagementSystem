package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AuthUserDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDto {

  private String id;
  private String email;
  private String name;
  private String roles;
}
