package com.example.order.controller;

import com.example.order.dto.ApiResponse;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderDto;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<ApiResponse<OrderDto>> create(@RequestBody @Valid CreateOrderRequest req) {
    OrderDto dto = orderService.createOrder(req.getUserId(), req.getItems(), "CREATED");
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderDto>> get(@PathVariable String id) {
    return orderService
        .findById(id)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
      @PathVariable String id, @RequestBody java.util.Map<String, String> body) {
    String status = body.get("status");
    return ResponseEntity.ok(ApiResponse.ok(orderService.updateStatus(id, status)));
  }
}
