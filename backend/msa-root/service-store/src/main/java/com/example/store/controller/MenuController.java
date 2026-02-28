package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.MenuDto;
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
  public ResponseEntity<ApiResponse<MenuDto>> create(@PathVariable String storeId,
      @RequestBody MenuDto req) {
    return ResponseEntity.ok(ApiResponse.ok(menuService.create(storeId, req)));
  }

  @PutMapping("/{menuId}")
  public ResponseEntity<ApiResponse<MenuDto>> update(@PathVariable String storeId,
      @PathVariable String menuId, @RequestBody MenuDto req) {
    return ResponseEntity.ok(ApiResponse.ok(menuService.update(storeId, menuId, req)));
  }

  @DeleteMapping("/{menuId}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String storeId,
      @PathVariable String menuId) {
    menuService.delete(storeId, menuId);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
