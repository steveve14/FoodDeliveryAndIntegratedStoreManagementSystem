package com.example.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoResponse {
    private String service;
    private String status;
    private String timestamp;

    public static InfoResponse up(String serviceName) {
        return new InfoResponse(serviceName, "UP", Instant.now().toString());
    }
}
