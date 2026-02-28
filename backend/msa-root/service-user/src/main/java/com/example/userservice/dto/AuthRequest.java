package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO for authentication requests (email + password).
 */
public class AuthRequest {
    private String email;
    private String password;
}
