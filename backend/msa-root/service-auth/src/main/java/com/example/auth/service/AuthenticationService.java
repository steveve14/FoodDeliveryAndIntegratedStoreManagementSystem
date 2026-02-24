package com.example.auth.service;

import com.example.auth.dto.TokenResponse;

public interface AuthenticationService {

    /**
     * 일반 이메일/비밀번호 로그인
     * @param email 이메일
     * @param password 비밀번호
     * @return 발급된 액세스/리프레시 토큰
     */
    TokenResponse login(String email, String password);

    /**
     * 소셜 로그인 통합 엔트리
     * @param provider 소셜 제공자 키워드 (예: "google", "kakao")
     * @param token 소셜에서 받은 인증 토큰 (ID Token 또는 access token 등)
     * @return 발급된 액세스/리프레시 토큰
     */
    TokenResponse socialLogin(String provider, String token);
}
