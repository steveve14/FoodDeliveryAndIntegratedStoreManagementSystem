package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
/** REST controller exposing user management endpoints. */
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody @Valid CreateUserRequest req) {
    UserDto dto = userService.register(req);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<UserDto>> getByEmail(@RequestParam String email) {
    UserDto dto = userService.findByEmail(email);
    if (dto == null) {
      return ResponseEntity.ok(ApiResponse.error("Not found"));
    }
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/{id}/profile")
  public ResponseEntity<ApiResponse<com.example.userservice.dto.UserProfileDto>> getProfile(
      @PathVariable String id) {
    com.example.userservice.dto.UserProfileDto dto = userService.getProfile(id);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @PutMapping("/{id}/profile")
  public ResponseEntity<ApiResponse<com.example.userservice.dto.UserProfileDto>> updateProfile(
      @PathVariable String id, @RequestBody com.example.userservice.dto.UpdateProfileRequest req) {
    com.example.userservice.dto.UserProfileDto dto = userService.updateProfile(id, req);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> withdraw(@PathVariable String id) {
    userService.withdraw(id);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
