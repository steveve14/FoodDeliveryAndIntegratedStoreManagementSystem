package com.example.apigateway.controller;

import com.example.apigateway.dto.ApiResponse;
import com.example.apigateway.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-gateway"));
  }
}
