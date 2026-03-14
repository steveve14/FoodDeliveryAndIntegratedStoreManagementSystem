package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** UserDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private String id;
  private String email;
  private String name;
  private String roles;
  private Instant createdAt;
}
