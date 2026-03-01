package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GoogleLoginRequest
 *
 * <p>역할: - 클라이언트가 구글 OAuth 인증 과정에서 전달하는 ID Token을 담는 단순 DTO(Data Transfer Object)입니다. -
 * 컨트롤러에서 @RequestBody로 바인딩하여 소셜 로그인을 처리할 때 사용됩니다.
 */
@Getter
@NoArgsConstructor
public class GoogleLoginRequest {
  // 클라이언트가 구글로부터 받아온 ID 토큰 문자열
  // 서버는 이 토큰을 구글 API로 검증하고, 검증 결과에 따라 사용자 정보를 추출합니다.
  @NotBlank(message = "Google ID 토큰을 입력해주세요.")
  private String idToken;
}
