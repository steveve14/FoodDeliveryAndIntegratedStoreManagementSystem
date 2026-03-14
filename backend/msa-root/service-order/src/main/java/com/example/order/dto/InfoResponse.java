package com.example.order.dto;

import java.time.Instant;
import lombok.Builder;

/** InfoResponse 타입입니다. */
@Builder
public class InfoResponse {
  private String service;
  private String status;
  private String timestamp;

  public InfoResponse() {}

  public InfoResponse(String service, String status, String timestamp) {
    this.service = service;
    this.status = status;
    this.timestamp = timestamp;
  }

  public static InfoResponse up(String serviceName) {
    return new InfoResponse(serviceName, "UP", Instant.now().toString());
  }

  // Lombok will provide getters/setters/constructors
}
