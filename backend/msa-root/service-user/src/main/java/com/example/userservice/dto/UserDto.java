package com.example.userservice.dto;

import java.time.Instant;

/**
 * 사용자 정보를 표현하는 DTO
 */
public record UserDto(Long id, String email, String name, String roles, Instant createdAt) {
}
