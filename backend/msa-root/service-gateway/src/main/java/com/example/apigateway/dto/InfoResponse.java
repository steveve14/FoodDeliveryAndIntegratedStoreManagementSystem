package com.example.apigateway.dto;

import java.time.Instant;

public record InfoResponse(String service, String status, String timestamp) {
    public static InfoResponse up(String serviceName) {
        return new InfoResponse(serviceName, "UP", Instant.now().toString());
    }
}
