package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FrontendUserDto 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendUserDto {
  private String id;
  private String name;
  private String email;
  private FrontendAvatarDto avatar;
  private String status;
  private String location;
}
