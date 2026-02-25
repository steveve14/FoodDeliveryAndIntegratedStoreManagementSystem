package com.example.order.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public class InfoResponse {
    private String service;
    private String status;
    private String timestamp;

    public InfoResponse() {}

    public InfoResponse(String service, String status, String timestamp) {
        this.service = service; this.status = status; this.timestamp = timestamp;
    }

    public static InfoResponse up(String serviceName) {
        return new InfoResponse(serviceName, "UP", Instant.now().toString());
    }

    // Lombok will provide getters/setters/constructors
}
