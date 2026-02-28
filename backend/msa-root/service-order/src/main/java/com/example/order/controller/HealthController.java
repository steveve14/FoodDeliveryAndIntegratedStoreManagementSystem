package com.example.order.controller;

import com.example.order.dto.ApiResponse;
import com.example.order.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-order"));
  }
}
