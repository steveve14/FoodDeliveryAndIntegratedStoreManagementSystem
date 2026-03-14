package com.example.delivery.controller;

import com.example.delivery.dto.ApiResponse;
import com.example.delivery.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 간단한 헬스 엔드포인트 제공
/** HealthController 타입입니다. */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-delivery"));
  }
}
