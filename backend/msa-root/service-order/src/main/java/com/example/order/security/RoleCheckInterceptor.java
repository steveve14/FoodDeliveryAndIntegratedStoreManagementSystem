package com.example.order.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Arrays;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** RoleCheckInterceptor 타입입니다. */
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

    // 메서드 → 클래스 순서로 @RequireRole 탐색
    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
    if (requireRole == null) {
      requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }
    boolean requiresAuthenticatedIdentity =
        requireRole != null
            || StringUtils.hasText(request.getHeader("X-User-Id"))
            || StringUtils.hasText(request.getHeader("X-User-Role"));
    if (!requiresAuthenticatedIdentity) {
      return true;
    }

    Claims claims = validateToken(request, response);
    if (claims == null) {
      return false;
    }

    String tokenUserId = claims.getSubject();
    Object roleClaim = claims.get("role");
    String tokenRole = roleClaim != null ? roleClaim.toString() : "";

    String headerUserId = request.getHeader("X-User-Id");
    if (StringUtils.hasText(headerUserId) && !headerUserId.equals(tokenUserId)) {
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "사용자 정보가 토큰과 일치하지 않습니다.");
      return false;
    }

    String headerUserRole = request.getHeader("X-User-Role");
    if (StringUtils.hasText(headerUserRole) && !headerUserRole.equals(tokenRole)) {
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "역할 정보가 토큰과 일치하지 않습니다.");
      return false;
    }

    if (requireRole == null) {
      return true;
    }

    boolean allowed = Arrays.asList(requireRole.value()).contains(tokenRole);
    if (!allowed) {
      log.warn(
          "Access denied: role '{}' not in {} for {}",
          tokenRole,
          Arrays.toString(requireRole.value()),
          request.getRequestURI());
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
      return false;
    }

    return true;
  }

  private Claims validateToken(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String token = resolveToken(request);
    if (!StringUtils.hasText(token)) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 토큰이 필요합니다.");
      return null;
    }

    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (Exception ex) {
      log.warn("Token validation failed for {}: {}", request.getRequestURI(), ex.getMessage());
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 인증 토큰입니다.");
      return null;
    }
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
