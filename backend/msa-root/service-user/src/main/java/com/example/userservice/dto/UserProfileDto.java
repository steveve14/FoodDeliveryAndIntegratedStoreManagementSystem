package com.example.userservice.dto;

/**
 * 사용자 프로필 조회 응답 DTO
 */
public record UserProfileDto(Long id, String email, String name, String phone) {
}
