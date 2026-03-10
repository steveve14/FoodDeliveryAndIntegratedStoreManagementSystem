package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendNotificationDto {
  private Long id;
  private boolean unread;
  private FrontendUserDto sender;
  private String body;
  private String date;
}