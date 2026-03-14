package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HealthController 타입입니다. */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @GetMapping("/info")
  public ApiResponse<InfoResponse> info() {
    return ApiResponse.ok(InfoResponse.up("service-store"));
  }
}
