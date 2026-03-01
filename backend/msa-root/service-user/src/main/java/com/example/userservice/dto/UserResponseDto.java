package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor // @Builder를 클래스 레벨에 쓸 때 @NoArgsConstructor와 세트로 써주는 것이 안전합니다.
/*
 DTO returned for user details responses.
*/
public class UserResponseDto {

  private String id;
  private String email;
  private String name;
  private String phone;
  private String roles;
  private Instant createdAt;
}
