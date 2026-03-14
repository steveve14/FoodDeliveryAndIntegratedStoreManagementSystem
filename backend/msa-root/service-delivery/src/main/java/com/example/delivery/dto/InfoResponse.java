package com.example.delivery.dto;

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

  /** 서비스 상태가 정상임을 나타내는 응답을 생성합니다. */
  public static InfoResponse up(String serviceName) {
    return new InfoResponse(serviceName, "UP", Instant.now().toString());
  }
}
