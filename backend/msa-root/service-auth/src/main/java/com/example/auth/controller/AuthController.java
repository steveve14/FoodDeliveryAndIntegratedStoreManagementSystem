package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.service.AuthenticationService;
import com.example.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * 역할:
 * - 인증 관련 HTTP 엔드포인트를 제공하는 REST 컨트롤러입니다.
 * - 실제 인증/토큰 발급 로직은 서비스 계층(AuthenticationService)에 위임합니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // 비즈니스 로직을 담당하는 서비스 주입 (생성자 주입, Lombok의 @RequiredArgsConstructor로 자동 생성)
    private final AuthenticationService authService;

    /*
      구글 소셜 로그인 엔드포인트
      - 클라이언트는 Google ID 토큰을 body에 담아 전송합니다.
      - 컨트롤러는 토큰을 서비스로 전달하고, 서비스는 토큰 검증 후 자체 JWT를 발급합니다.
     */

//    @PostMapping("/google")
    // Google 소셜 로그인은 현재 비활성화(주석 처리)
    // public ResponseEntity<TokenResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
    //     // 서비스로 토큰 검증 및 로그인 처리 위임
    //     TokenResponse tokenResponse = authService.socialLogin("google", request.getIdToken());
    //     return ResponseEntity.ok(tokenResponse);
    // }

    /**
     * 일반적인 소셜 로그인 엔드포인트
     * - provider 파라미터로 소셜 제공자를 구분하고, token 파라미터에 소셜에서 발급한 토큰을 전달합니다.
     */
    @PostMapping("/social")
    public ResponseEntity<TokenResponse> socialLogin(@RequestParam String provider, @RequestParam String token) {
        TokenResponse tokenResponse = authService.socialLogin(provider, token);
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * 이메일/비밀번호 로그인 엔드포인트
     * - LoginRequest DTO에서 이메일과 비밀번호를 받아 서비스에 전달합니다.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }
}