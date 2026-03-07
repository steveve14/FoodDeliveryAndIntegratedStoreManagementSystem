package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.AuthRequest;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
/** Controller exposing authentication endpoints for users. */
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/authenticate")
  public ResponseEntity<ApiResponse<com.example.userservice.dto.AuthUserDto>> authenticate(
      @RequestBody @Valid AuthRequest req) {
    com.example.userservice.dto.AuthUserDto dto =
        userService.authenticate(req.getEmail(), req.getPassword());
    if (dto == null) {
      return ResponseEntity.status(401).body(ApiResponse.error(401, "Unauthorized"));
    }
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }
}
