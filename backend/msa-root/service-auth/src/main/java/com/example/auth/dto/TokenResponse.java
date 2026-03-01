// src/main/java/com/example/auth/dto/TokenResponse.java
package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TokenResponse
 *
 * <p>역할: - 인증 성공 시 클라이언트에게 반환하는 단순 DTO로, 액세스 토큰과 리프레시 토큰을 포함합니다.
 *
 * <p>Lombok 어노테이션: - @Getter: 모든 필드에 대한 getter를 자동 생성합니다. - @NoArgsConstructor: 기본 생성자 생성
 * - @AllArgsConstructor: 모든 필드를 인수로 받는 생성자 생성(테스트/간편 반환에 유용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor // 이 어노테이션 덕분에 new TokenResponse("access", "refresh") 가 가능해집니다!
public class TokenResponse {
  // 발급된 액세스 토큰 문자열
  private String accessToken;
  // 발급된 리프레시 토큰 문자열
  private String refreshToken;

  // 나중에 필요하면 이런 필드도 쉽게 추가할 수 있습니다.
  // private String grantType = "Bearer";
  // private Long expiresIn;
}
