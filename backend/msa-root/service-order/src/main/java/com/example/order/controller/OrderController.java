package com.example.order.controller;

import com.example.order.dto.ApiResponse;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.FrontendCustomerOrderSummaryDto;
import com.example.order.dto.OrderDto;
import com.example.order.security.RequireRole;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** OrderController 타입입니다. */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  /** 주문 컨트롤러를 생성합니다. */
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  /** 주문을 생성합니다. */
  @PostMapping
  @RequireRole({"USER", "STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<OrderDto>> create(
      @RequestHeader("X-User-Id") String userId, @RequestBody @Valid CreateOrderRequest req) {
    OrderDto dto = orderService.createOrder(userId, req.getItems(), "CREATED");
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  /** ID로 주문을 조회합니다. */
  @GetMapping("/{id}")
  @RequireRole({"USER", "STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<OrderDto>> get(
      @PathVariable String id,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole) {
    return orderService
        .findById(id)
        .map(
            dto -> {
              if (!orderService.canAccessOrder(dto, userId, userRole)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
              }
              return ResponseEntity.ok(ApiResponse.ok(dto));
            })
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }

  /** 현재 사용자의 주문 목록을 조회합니다. */
  @GetMapping("/my")
  @RequireRole({"USER", "ADMIN"})
  public ResponseEntity<ApiResponse<java.util.List<OrderDto>>> getMyOrders(
      @RequestHeader("X-User-Id") String userId) {
    return ResponseEntity.ok(ApiResponse.ok(orderService.findByUserId(userId)));
  }

  /** 프런트엔드 고객 주문 요약 목록을 조회합니다. */
  @GetMapping("/frontend/customer-summaries")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<java.util.List<FrontendCustomerOrderSummaryDto>>>
      getFrontendCustomerSummaries(
          @RequestHeader("X-User-Id") String userId,
          @RequestHeader("X-User-Role") String userRole) {
    return ResponseEntity.ok(
        ApiResponse.ok(orderService.getFrontendCustomerSummaries(userId, userRole)));
  }

  /** 매장별 주문 목록을 조회합니다. */
  @GetMapping("/store/{storeId}")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<java.util.List<OrderDto>>> getStoreOrders(
      @PathVariable String storeId,
      @RequestHeader("X-User-Id") String userId,
      @RequestHeader("X-User-Role") String userRole) {
    return ResponseEntity.ok(ApiResponse.ok(orderService.findByStoreId(storeId, userId, userRole)));
  }

  /** 주문 상태를 변경합니다. */
  @PatchMapping("/{id}/status")
  @RequireRole({"STORE", "ADMIN"})
  public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
      @PathVariable String id, @RequestBody java.util.Map<String, String> body) {
    String status = body.get("status");
    return ResponseEntity.ok(ApiResponse.ok(orderService.updateStatus(id, status)));
  }
}
