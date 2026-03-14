package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.CartItemDto;
import com.example.userservice.dto.CartItemRequest;
import com.example.userservice.security.RequireRole;
import com.example.userservice.service.CartService;
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

/** CartController 타입입니다. */
@RestController
@RequestMapping("/api/v1/users/me/cart")
@RequiredArgsConstructor
@RequireRole({"USER", "ADMIN"})
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<ApiResponse<java.util.List<CartItemDto>>> list(
      @RequestHeader("X-User-Id") String userId) {
    return ResponseEntity.ok(ApiResponse.ok(cartService.listByUser(userId)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CartItemDto>> add(
      @RequestHeader("X-User-Id") String userId, @RequestBody @Valid CartItemRequest request) {
    return ResponseEntity.ok(ApiResponse.ok(cartService.addItem(userId, request)));
  }

  @DeleteMapping("/{cartItemId}")
  public ResponseEntity<ApiResponse<Object>> delete(
      @RequestHeader("X-User-Id") String userId, @PathVariable String cartItemId) {
    cartService.deleteItem(userId, cartItemId);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Object>> clear(@RequestHeader("X-User-Id") String userId) {
    cartService.clear(userId);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
