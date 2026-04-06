package com.example.event.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** 이벤트 서비스 요청의 JWT와 역할을 검증합니다. */
@Slf4j
@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

  private final SecretKey secretKey;

  /** JWT 서명 키를 초기화합니다. */
  public RoleCheckInterceptor(Environment env) {
    String secret = env.getProperty("token.secret", env.getProperty("TOKEN_SECRET"));
    if (!StringUtils.hasText(secret)) {
      throw new IllegalStateException("token.secret is empty. Set TOKEN_SECRET environment variable.");
    }
    try {
      this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("TOKEN_SECRET must be a valid Base64-encoded key.", e);
    }
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }

    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
    if (requireRole == null) {
      requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }
    if (requireRole == null) {
      return true;
    }

    String token = resolveToken(request);
    if (!StringUtils.hasText(token)) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 토큰이 필요합니다.");
      return false;
    }

    String tokenRole;
    try {
      Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
      Object roleClaim = claims.get("role");
      tokenRole = roleClaim != null ? roleClaim.toString() : "";
    } catch (Exception ex) {
      log.warn("Token validation failed for {}: {}", request.getRequestURI(), ex.getMessage());
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 인증 토큰입니다.");
      return false;
    }

    if (!Arrays.asList(requireRole.value()).contains(tokenRole)) {
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
      return false;
    }

    return true;
  }

  private String resolveToken(HttpServletRequest request) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    if (request.getCookies() == null) {
      return null;
    }
    return Arrays.stream(request.getCookies())
        .filter(cookie -> "access-token".equals(cookie.getName()))
        .map(jakarta.servlet.http.Cookie::getValue)
        .filter(StringUtils::hasText)
        .findFirst()
        .orElse(null);
  }

  private void writeError(HttpServletResponse response, int status, String message)
      throws Exception {
    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
  }
}