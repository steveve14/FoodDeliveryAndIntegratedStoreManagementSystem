package com.example.delivery.dto;

import java.time.Instant;

/**
 * 서비스 상태 응답 DTO
 * 간단한 서비스 이름, 상태, 타임스탬프를 포함합니다.
 */
public record InfoResponse(String service, String status, String timestamp) {
    public static InfoResponse up(String serviceName) {
        return new InfoResponse(serviceName, "UP", Instant.now().toString());
    }
}
