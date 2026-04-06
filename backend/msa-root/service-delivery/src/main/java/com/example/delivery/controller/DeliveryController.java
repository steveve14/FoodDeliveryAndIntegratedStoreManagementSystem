package com.example.delivery.controller;

import com.example.delivery.dto.ApiResponse;
import com.example.delivery.dto.CreateDeliveryRequest;
import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.security.RequireRole;
import com.example.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DeliveryController 타입입니다. */
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

  private final DeliveryService deliveryService;

  /** 컨트롤러 의존성을 주입합니다. */
  public DeliveryController(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  /** 배달을 생성합니다. */
  @PostMapping
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<DeliveryDto>> create(
      @RequestBody @Valid CreateDeliveryRequest req) {
    DeliveryDto dto =
        deliveryService.createDelivery(req.getOrderId(), req.getCourier(), "SCHEDULED");
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  /** 배달 ID로 배달 정보를 조회합니다. */
  @GetMapping("/{id}")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<DeliveryDto>> get(@PathVariable String id) {
    return deliveryService
        .findById(id)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }

  /** 배달 상태를 업데이트합니다. */
  @PatchMapping("/{id}/status")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<DeliveryDto>> updateStatus(
      @PathVariable String id, @RequestBody java.util.Map<String, String> body) {
    String status = body.get("status");
    DeliveryDto dto = deliveryService.updateStatus(id, status);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }
}
