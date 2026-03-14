package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** UserCreateRequestDto 타입입니다. */
@Getter
@Setter
@NoArgsConstructor // JSON 직렬화/역직렬화를 위해 기본 생성자 필수
public class UserCreateRequestDto {
  private String email;
  private String password;
  private String name;
  private String phone;
}
