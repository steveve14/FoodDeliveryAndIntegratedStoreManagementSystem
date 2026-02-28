package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/**
 * DTO for user profile update requests.
 */
public class UserUpdateRequestDto {
    private String name;
    private String phone;
}