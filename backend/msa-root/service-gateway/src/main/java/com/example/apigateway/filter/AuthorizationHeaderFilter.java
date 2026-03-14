package com.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** AuthorizationHeaderFilter 타입입니다. */
@Component
@Slf4j
public class AuthorizationHeaderFilter
    extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  private final SecretKey secretKey;

  /** 인증 필터를 생성하고 JWT 서명 키를 초기화합니다. */
  public AuthorizationHeaderFilter(Environment env) {
    super(Config.class);
    String secret = env.getProperty("token.secret");
    if (!StringUtils.hasText(secret)) {
      throw new IllegalStateException(
          "token.secret is empty. Set TOKEN_SECRET environment variable.");
    }
    try {
      byte[] keyBytes = Base64.getDecoder().decode(secret);
      this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("TOKEN_SECRET must be a valid Base64-encoded key.", e);
    }
  }

  /** 게이트웨이 인증 필터 설정 타입입니다. */
  public static class Config {
    // 설정 정보가 필요하면 여기에 추가
  }

  /** 게이트웨이 요청에 대한 JWT 검증 필터를 반환합니다. */
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // 1. Authorization 헤더 또는 access-token 쿠키에서 JWT 추출
      String jwt = resolveToken(request);
      if (jwt == null) {
        return onError(exchange, "No authorization header/cookie or invalid format");
      }

      // 2. 토큰 검증 및 클레임 추출
      Claims claims = getClaimsFromToken(jwt);
      if (claims == null) {
        return onError(exchange, "JWT token is not valid");
      }

      String userId = claims.getSubject();
      Object role = claims.get("role");
      String userRole = role != null ? role.toString() : "";

      // 4. 유저 ID 및 역할 전달
      ServerHttpRequest newRequest =
          request.mutate().header("X-User-Id", userId).header("X-User-Role", userRole).build();

      return chain.filter(exchange.mutate().request(newRequest).build());
    };
  }

  private Claims getClaimsFromToken(String jwt) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
    } catch (Exception ex) {
      log.error("Token validation error: {}", ex.getMessage());
      return null;
    }
  }

  /** Authorization 헤더에서 먼저, 없으면 access-token 쿠키에서 JWT를 추출합니다. */
  private String resolveToken(ServerHttpRequest request) {
    // 1) Authorization: Bearer <token> 헤더 우선
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    // 2) httpOnly 쿠키 fallback
    HttpCookie cookie = request.getCookies().getFirst("access-token");
    if (cookie != null && StringUtils.hasText(cookie.getValue())) {
      return cookie.getValue();
    }
    return null;
  }

  private Mono<Void> onError(ServerWebExchange exchange, String err) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    log.error(err);
    return response.setComplete();
  }
}
