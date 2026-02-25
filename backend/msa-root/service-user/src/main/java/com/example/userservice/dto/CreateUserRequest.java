package com.example.userservice.dto;

/**
 * 사용자 생성 요청 DTO
 */
public record CreateUserRequest(String email, String password, String name) {
}
