package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequest req) {
        UserDto dto = userService.register(req);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {
        UserDto dto = userService.findByEmail(email);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<com.example.userservice.dto.UserProfileDto> getProfile(@PathVariable Long id) {
        com.example.userservice.dto.UserProfileDto dto = userService.getProfile(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<com.example.userservice.dto.UserProfileDto> updateProfile(@PathVariable Long id, @RequestBody com.example.userservice.dto.UpdateProfileRequest req) {
        com.example.userservice.dto.UserProfileDto dto = userService.updateProfile(id, req);
        return ResponseEntity.ok(dto);
    }
}
