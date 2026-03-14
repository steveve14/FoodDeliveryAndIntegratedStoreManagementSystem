package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** Simple DTO representing user identity and metadata. */
/** UserDto 타입입니다. */
public class UserDto {

  private String id;
  private String email;
  private String name;
  private String roles;
  private Instant createdAt;
}
