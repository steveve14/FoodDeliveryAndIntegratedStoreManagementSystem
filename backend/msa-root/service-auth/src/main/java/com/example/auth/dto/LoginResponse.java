package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 로그인 성공 시 사용자 정보만 반환하는 DTO (토큰은 httpOnly 쿠키로 전송) */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private String id;
  private String email;
  private String name;
  private String role;
}
