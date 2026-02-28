package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JSON 직렬화/역직렬화를 위해 기본 생성자 필수
/**
 * DTO for creating a new user (registration request).
 */
public class UserCreateRequestDto {
    private String email;
    private String password;
    private String name;
    private String phone;
}