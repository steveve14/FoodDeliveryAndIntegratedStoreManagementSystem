package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.FavoriteStoreDto;
import com.example.userservice.dto.FavoriteStoreRequest;
import com.example.userservice.security.RequireRole;
import com.example.userservice.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
@RequiredArgsConstructor
@RequireRole({ "USER", "ADMIN" })
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<FavoriteStoreDto>>> list(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.listByUser(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteStoreDto>> add(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid FavoriteStoreRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.add(userId, request)));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String favoriteId) {
        favoriteService.delete(userId, favoriteId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}