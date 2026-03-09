package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.MenuDto;
import com.example.store.security.RequireRole;
import com.example.store.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<MenuDto>>> list(@PathVariable String storeId) {
    return ResponseEntity.ok(ApiResponse.ok(menuService.listByStore(storeId)));
  }

  @PostMapping
  @RequireRole({ "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<MenuDto>> create(
      @PathVariable String storeId,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody MenuDto req) {
    return ResponseEntity.ok(ApiResponse.ok(menuService.create(storeId, userId, userRole, req)));
  }

  @PutMapping("/{menuId}")
  @RequireRole({ "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<MenuDto>> update(
      @PathVariable String storeId,
      @PathVariable String menuId,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody MenuDto req) {
    return ResponseEntity.ok(ApiResponse.ok(menuService.update(storeId, menuId, userId, userRole, req)));
  }

  @DeleteMapping("/{menuId}")
  @RequireRole({ "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<Object>> delete(
      @PathVariable String storeId,
      @PathVariable String menuId,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole) {
    menuService.delete(storeId, menuId, userId, userRole);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
