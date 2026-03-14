package com.example.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AuthCheckRequest 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthCheckRequest {
  private String token;
}
