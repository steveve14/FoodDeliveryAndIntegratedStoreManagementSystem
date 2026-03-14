package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.CreateStoreRequest;
import com.example.store.dto.StoreDto;
import com.example.store.security.RequireRole;
import com.example.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
/** REST controller exposing store management endpoints. */
/** StoreController 타입입니다. */
public class StoreController {

  private final StoreService storeService;

  public StoreController(StoreService storeService) {
    this.storeService = storeService;
  }

  @PostMapping
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<StoreDto>> create(
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody @Valid CreateStoreRequest req) {
    StoreDto dto = storeService.createStore(req, userId, userRole);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<java.util.List<StoreDto>>> list(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String status) {
    return ResponseEntity.ok(ApiResponse.ok(storeService.list(category, status)));
  }

  @GetMapping("/me")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<StoreDto>> getMyStore(
      @RequestHeader("X-User-Id") String userId) {
    return storeService
        .findByOwnerId(userId)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<StoreDto>> get(@PathVariable String id) {
    return storeService
        .findById(id)
        .map(d -> ResponseEntity.ok(ApiResponse.ok(d)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }
}
