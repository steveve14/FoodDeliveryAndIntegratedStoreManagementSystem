package com.example.order.dto;

import java.time.Instant;
import lombok.Builder;

/** InfoResponse 타입입니다. */
@Builder
public class InfoResponse {
  private String service;
  private String status;
  private String timestamp;

  /** 기본 생성자입니다. */
  public InfoResponse() {}

  /** 서비스 정보 응답을 생성합니다. */
  public InfoResponse(String service, String status, String timestamp) {
    this.service = service;
    this.status = status;
    this.timestamp = timestamp;
  }

  /** 서비스 구동 상태 정보를 생성합니다. */
  public static InfoResponse up(String serviceName) {
    return new InfoResponse(serviceName, "UP", Instant.now().toString());
  }

  // Lombok will provide getters/setters/constructors
}
