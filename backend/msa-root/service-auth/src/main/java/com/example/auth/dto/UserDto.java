package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/// Jwt 발급/응답 등에서 사용할 수 있는 간단한 사용자 DTO - id: 내부 사용자 식별자 - email: 사용자 이메일 - name: 사용자 이름 - roles: 콤마
/// 구분 권한 문자열(간단 표현)
/** UserDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String id;
  private String email;
  private String name;
  private String roles;
  // Lombok will generate constructors/getters/setters
}
