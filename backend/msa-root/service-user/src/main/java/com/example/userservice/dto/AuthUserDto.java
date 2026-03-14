package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** DTO returned after successful authentication containing basic user info. */
/** AuthUserDto 타입입니다. */
public class AuthUserDto {

  private String id;
  private String email;
  private String name;
  private String roles;
}
