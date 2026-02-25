package com.example.userservice.controller;

import com.example.userservice.dto.AuthRequest;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<com.example.userservice.dto.AuthUserDto> authenticate(@RequestBody AuthRequest req) {
        com.example.userservice.dto.AuthUserDto dto = userService.authenticate(req.getEmail(), req.getPassword());
        if (dto == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(dto);
    }
}
