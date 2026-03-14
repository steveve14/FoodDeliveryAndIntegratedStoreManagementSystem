package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/** DTO for user profile update requests. */
/** UserUpdateRequestDto 타입입니다. */
public class UserUpdateRequestDto {
  private String name;
  private String phone;
}
