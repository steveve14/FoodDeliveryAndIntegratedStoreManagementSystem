package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FrontendMemberDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendMemberDto {
  private String name;
  private String username;
  private String role;
  private FrontendAvatarDto avatar;
}
