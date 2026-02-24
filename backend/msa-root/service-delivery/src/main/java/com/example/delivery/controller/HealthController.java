package com.example.delivery.controller;

import com.example.delivery.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 간단한 헬스 엔드포인트 제공
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/info")
    public InfoResponse info() {
        return InfoResponse.up("service-delivery");
    }
}
