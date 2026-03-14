package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** LogoutRequest 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

  @NotBlank(message = "Refresh 토큰을 입력해주세요.")
  private String refreshToken;
}
