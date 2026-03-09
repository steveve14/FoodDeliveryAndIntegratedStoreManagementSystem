package com.example.order.controller;

import com.example.order.dto.ApiResponse;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderDto;
import com.example.order.security.RequireRole;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @RequireRole({ "USER", "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<OrderDto>> create(
      @RequestHeader("X-User-Id") String userId, @RequestBody @Valid CreateOrderRequest req) {
    OrderDto dto = orderService.createOrder(userId, req.getItems(), "CREATED");
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderDto>> get(@PathVariable String id) {
    return orderService
        .findById(id)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }

  @GetMapping("/my")
  @RequireRole({ "USER", "ADMIN" })
  public ResponseEntity<ApiResponse<java.util.List<OrderDto>>> getMyOrders(
      @RequestHeader("X-User-Id") String userId) {
    return ResponseEntity.ok(ApiResponse.ok(orderService.findByUserId(userId)));
  }

  @GetMapping("/store/{storeId}")
  @RequireRole({ "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<java.util.List<OrderDto>>> getStoreOrders(
      @PathVariable String storeId,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole) {
    return ResponseEntity.ok(ApiResponse.ok(orderService.findByStoreId(storeId, userId, userRole)));
  }

  @PatchMapping("/{id}/status")
  @RequireRole({ "STORE", "ADMIN" })
  public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
      @PathVariable String id, @RequestBody java.util.Map<String, String> body) {
    String status = body.get("status");
    return ResponseEntity.ok(ApiResponse.ok(orderService.updateStatus(id, status)));
  }
}
