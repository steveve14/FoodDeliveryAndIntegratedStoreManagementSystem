package com.example.auth.service;

import com.example.auth.dto.LoginResult;
import com.example.auth.dto.TokenResponse;

/** AuthenticationService 타입입니다. */
public interface AuthenticationService {

  /** 일반 이메일/비밀번호 로그인입니다. */
  LoginResult login(String email, String password);

  /** 소셜 로그인 엔트리입니다. */
  LoginResult socialLogin(String provider, String token);

  /** 리프레시 토큰으로 토큰을 재발급합니다. */
  TokenResponse refresh(String refreshToken);

  /** 리프레시 토큰을 무효화합니다. */
  void logout(String refreshToken);

  /** 회원가입 후 토큰을 발급합니다. */
  LoginResult signup(String email, String password, String name);
}
