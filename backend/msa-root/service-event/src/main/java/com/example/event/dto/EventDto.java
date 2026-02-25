package com.example.event.dto;

import java.time.Instant;

public record EventDto(Long id, String type, String payload, Instant createdAt) {
}
