package com.example.auth.service;

import com.example.auth.dto.LoginResult;
import com.example.auth.dto.TokenResponse;

/** AuthenticationService 타입입니다. */
public interface AuthenticationService {

  /// 일반 이메일/비밀번호 로그인
  ///
  /// @param email 이메일
  /// @param password 비밀번호
  /// @return 토큰 + 사용자 정보
  LoginResult login(String email, String password);

  /// 소셜 로그인 통합 엔트리
  ///
  /// @param provider 소셜 제공자 키워드 (예: "google", "kakao")
  /// @param token 소셜에서 받은 인증 토큰 (ID Token 또는 access token 등)
  /// @return 토큰 + 사용자 정보
  LoginResult socialLogin(String provider, String token);

  // Token management
  TokenResponse refresh(String refreshToken);

  void logout(String refreshToken);

  /// 회원가입 — DB 저장 후 검증 완료 시 토큰 발급
  ///
  /// @param email 이메일
  /// @param password 비밀번호
  /// @param name 이름
  /// @return 토큰 + 사용자 정보
  LoginResult signup(String email, String password, String name);
}
