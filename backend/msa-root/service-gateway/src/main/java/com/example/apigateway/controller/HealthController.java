package com.example.apigateway.controller;

import com.example.apigateway.dto.ApiResponse;
import com.example.apigateway.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HealthController 타입입니다. */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

  /** 서비스 상태 정보를 반환합니다. */
  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-gateway"));
  }
}
