package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 인증 결과로 토큰과 사용자 정보를 함께 전달합니다. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
  private TokenResponse tokens;
  private String userId;
  private String email;
  private String name;
  private String role;
}
