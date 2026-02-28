package com.example.discovery.controller;

import com.example.discovery.dto.ApiResponse;
import com.example.discovery.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-discovery"));
  }
}
