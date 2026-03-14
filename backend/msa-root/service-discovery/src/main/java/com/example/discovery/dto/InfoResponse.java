package com.example.discovery.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** InfoResponse 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoResponse {
  private String service;
  private String status;
  private String timestamp;

  /** 서비스 구동 상태 정보를 생성합니다. */
  public static InfoResponse up(String serviceName) {
    return new InfoResponse(serviceName, "UP", Instant.now().toString());
  }
}
