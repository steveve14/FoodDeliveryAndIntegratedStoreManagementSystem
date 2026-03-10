package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendMailDto {
  private Long id;
  private boolean unread;
  private FrontendUserDto from;
  private String subject;
  private String body;
  private String date;
}