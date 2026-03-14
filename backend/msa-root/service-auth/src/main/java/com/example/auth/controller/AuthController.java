package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;
import com.example.auth.dto.LoginResult;
import com.example.auth.dto.SignupRequest;
import com.example.auth.dto.TokenResponse;
import com.example.auth.grpc.client.UserGrpcClient;
import com.example.auth.security.JwtProvider;
import com.example.auth.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** AuthController 타입입니다. */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {

  private final AuthenticationService authService;
  private final JwtProvider jwtProvider;
  private final UserGrpcClient userGrpcClient;

  @Value("${token.expiration_time:3600000}")
  private long accessTokenValidityMs;

  @Value("${token.refresh_time:1209600000}")
  private long refreshTokenValidityMs;

  @PostMapping("/social")
  ResponseEntity<ApiResponse<LoginResponse>> socialLogin(
      @RequestParam String provider, @RequestParam String token, HttpServletResponse response) {
    LoginResult result = authService.socialLogin(provider, token);
    addTokenCookies(response, result.getTokens());
    return ResponseEntity.ok(ApiResponse.ok(buildLoginResponse(result)));
  }

  @PostMapping("/login")
  ResponseEntity<ApiResponse<LoginResponse>> login(
      @RequestBody @Valid LoginRequest request, HttpServletResponse response) {
    LoginResult result = authService.login(request.getEmail(), request.getPassword());
    addTokenCookies(response, result.getTokens());
    return ResponseEntity.ok(ApiResponse.ok(buildLoginResponse(result)));
  }

  @PostMapping("/refresh")
  ResponseEntity<ApiResponse<LoginResponse>> refresh(
      HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractCookie(request, "refresh-token");
    if (refreshToken == null || refreshToken.isBlank()) {
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Refresh 토큰이 필요합니다."));
    }
    TokenResponse tokens = authService.refresh(refreshToken);
    addTokenCookies(response, tokens);
    return ResponseEntity.ok(ApiResponse.ok(buildLoginResponse(tokens)));
  }

  @PostMapping("/logout")
  ResponseEntity<ApiResponse<Object>> logout(
      HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractCookie(request, "refresh-token");
    if (refreshToken != null) {
      authService.logout(refreshToken);
    }
    clearTokenCookies(response);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  @PostMapping("/signup")
  ResponseEntity<ApiResponse<LoginResponse>> signup(
      @RequestBody @Valid SignupRequest req, HttpServletResponse response) {
    LoginResult result = authService.signup(req.getEmail(), req.getPassword(), req.getName());
    addTokenCookies(response, result.getTokens());
    return ResponseEntity.ok(ApiResponse.ok(buildLoginResponse(result)));
  }

  // ── 쿠키 헬퍼 ──────────────────────────────────────────

  private void addTokenCookies(HttpServletResponse response, TokenResponse tokens) {
    Cookie accessCookie = new Cookie("access-token", tokens.getAccessToken());
    accessCookie.setHttpOnly(true);
    accessCookie.setSecure(false); // 개발 환경 (운영 시 true)
    accessCookie.setPath("/");
    accessCookie.setMaxAge((int) (accessTokenValidityMs / 1000));
    response.addCookie(accessCookie);

    Cookie refreshCookie = new Cookie("refresh-token", tokens.getRefreshToken());
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(false); // 개발 환경 (운영 시 true)
    refreshCookie.setPath("/api/v1/auth");
    refreshCookie.setMaxAge((int) (refreshTokenValidityMs / 1000));
    response.addCookie(refreshCookie);
  }

  private void clearTokenCookies(HttpServletResponse response) {
    Cookie accessCookie = new Cookie("access-token", "");
    accessCookie.setHttpOnly(true);
    accessCookie.setPath("/");
    accessCookie.setMaxAge(0);
    response.addCookie(accessCookie);

    Cookie refreshCookie = new Cookie("refresh-token", "");
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/api/v1/auth");
    refreshCookie.setMaxAge(0);
    response.addCookie(refreshCookie);
  }

  private String extractCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return null;
    for (Cookie c : request.getCookies()) {
      if (name.equals(c.getName())) return c.getValue();
    }
    return null;
  }

  private LoginResponse buildLoginResponse(LoginResult result) {
    return new LoginResponse(
        result.getUserId(), result.getEmail(), result.getName(), result.getRole());
  }

  private LoginResponse buildLoginResponse(TokenResponse tokens) {
    String userId = jwtProvider.getUserIdFromToken(tokens.getAccessToken());
    String role = jwtProvider.getRolesFromToken(tokens.getAccessToken());

    var userResponse = userGrpcClient.getUserById(userId);
    if (userResponse.getFound()) {
      String resolvedRole = userResponse.getRoles().isBlank() ? role : userResponse.getRoles();
      return new LoginResponse(
          userResponse.getUserId(), userResponse.getEmail(), userResponse.getName(), resolvedRole);
    }

    return new LoginResponse(userId, "", "", role);
  }
}
