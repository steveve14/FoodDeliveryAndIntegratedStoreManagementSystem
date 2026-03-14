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

/** FavoriteController 타입입니다. */
@RestController
@RequestMapping("/api/v1/users/me/favorites")
@RequiredArgsConstructor
@RequireRole({"USER", "ADMIN"})
public class FavoriteController {

  private final FavoriteService favoriteService;

  /** 현재 사용자의 찜한 가게 목록을 조회합니다. */
  @GetMapping
  public ResponseEntity<ApiResponse<java.util.List<FavoriteStoreDto>>> list(
      @RequestHeader("X-User-Id") String userId) {
    return ResponseEntity.ok(ApiResponse.ok(favoriteService.listByUser(userId)));
  }

  /** 가게를 찜 목록에 추가합니다. */
  @PostMapping
  public ResponseEntity<ApiResponse<FavoriteStoreDto>> add(
      @RequestHeader("X-User-Id") String userId, @RequestBody @Valid FavoriteStoreRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(favoriteService.add(userId, request)));
  }

  /** 찜한 가게를 삭제합니다. */
  @DeleteMapping("/{favoriteId}")
  public ResponseEntity<ApiResponse<Object>> delete(
      @RequestHeader("X-User-Id") String userId, @PathVariable String favoriteId) {
    favoriteService.delete(userId, favoriteId);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
