package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** UserUpdateRequestDto 타입입니다. */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {
  private String name;
  private String phone;
}
