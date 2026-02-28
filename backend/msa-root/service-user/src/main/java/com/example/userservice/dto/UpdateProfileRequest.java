package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO for updating a user's profile information.
 */
public class UpdateProfileRequest {
	private String name;
	private String phone;
}
