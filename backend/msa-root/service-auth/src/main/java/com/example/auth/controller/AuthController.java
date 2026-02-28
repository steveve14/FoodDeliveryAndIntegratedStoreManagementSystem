package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.TokenResponse;
import com.example.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {

  private final AuthenticationService authService;

  @PostMapping("/social")
  ResponseEntity<ApiResponse<TokenResponse>> socialLogin(@RequestParam String provider,
      @RequestParam String token) {
    return ResponseEntity.ok(ApiResponse.ok(authService.socialLogin(provider, token)));
  }

  @PostMapping("/login")
  ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(
        ApiResponse.ok(authService.login(request.getEmail(), request.getPassword())));
  }

  @PostMapping("/refresh")
  ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(authService.refresh(req.getRefreshToken())));
  }

  @PostMapping("/logout")
  ResponseEntity<ApiResponse<Object>> logout(@RequestBody LogoutRequest req) {
    authService.logout(req.getRefreshToken());
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}