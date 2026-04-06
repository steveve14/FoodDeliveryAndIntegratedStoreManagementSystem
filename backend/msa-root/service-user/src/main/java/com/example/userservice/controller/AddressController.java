package com.example.userservice.controller;

import com.example.userservice.dto.AddressDto;
import com.example.userservice.dto.ApiResponse;
import com.example.userservice.security.RequireRole;
import com.example.userservice.service.AddressService;
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

/** AddressController 타입입니다. */
@RestController
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
@RequireRole({"USER", "ADMIN"})
public class AddressController {

  private final AddressService addressService;

  /** 현재 사용자의 주소 목록을 조회합니다. */
  @GetMapping
  public ResponseEntity<ApiResponse<java.util.List<AddressDto>>> list(
      @RequestHeader("X-User-Id") String userId) {
    return ResponseEntity.ok(ApiResponse.ok(addressService.listByUser(userId)));
  }

  /** 현재 사용자 주소를 생성합니다. */
  @PostMapping
  public ResponseEntity<ApiResponse<AddressDto>> create(
      @RequestHeader("X-User-Id") String userId, @RequestBody AddressDto req) {
    return ResponseEntity.ok(ApiResponse.ok(addressService.create(userId, req)));
  }

  /** 현재 사용자 주소를 수정합니다. */
  @PutMapping("/{addressId}")
  public ResponseEntity<ApiResponse<AddressDto>> update(
      @RequestHeader("X-User-Id") String userId,
      @PathVariable String addressId,
      @RequestBody AddressDto req) {
    return ResponseEntity.ok(ApiResponse.ok(addressService.update(userId, addressId, req)));
  }

  /** 현재 사용자 주소를 삭제합니다. */
  @DeleteMapping("/{addressId}")
  public ResponseEntity<ApiResponse<Object>> delete(
      @RequestHeader("X-User-Id") String userId, @PathVariable String addressId) {
    addressService.delete(userId, addressId);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
