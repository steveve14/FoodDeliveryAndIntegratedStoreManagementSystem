package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.FrontendCustomerOrderSummaryDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.grpc.client.StoreGrpcClient;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final StoreGrpcClient storeGrpcClient;
  private final JdbcTemplate jdbcTemplate;

  // 주문 상태 전이 규칙: CREATED → COOKING → DELIVERING → DONE | CANCELLED
  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
      "CREATED", Set.of("COOKING", "CANCELLED"),
      "COOKING", Set.of("DELIVERING", "CANCELLED"),
      "DELIVERING", Set.of("DONE", "CANCELLED"));

  public OrderService(
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      StoreGrpcClient storeGrpcClient,
      JdbcTemplate jdbcTemplate) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.storeGrpcClient = storeGrpcClient;
    this.jdbcTemplate = jdbcTemplate;
  }

  public java.util.List<FrontendCustomerOrderSummaryDto> getFrontendCustomerSummaries() {
    return jdbcTemplate.query(
        """
        SELECT user_id, COUNT(*) AS orders_count, MAX(created_at) AS last_order_at
        FROM orders
        WHERE user_id IS NOT NULL
        GROUP BY user_id
        ORDER BY last_order_at DESC
        """,
        (rs, rowNum) ->
            new FrontendCustomerOrderSummaryDto(
                rs.getString("user_id"),
                rs.getLong("orders_count"),
                rs.getTimestamp("last_order_at").toInstant(),
                rs.getLong("orders_count") >= 10 ? "vip" : "regular"));
  }

  @Transactional
  public OrderDto createOrder(String userId, java.util.List<OrderItemDto> items, String status) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Order must contain items");
    }

    // Validate first item to get storeId
    String menuId = items.get(0).getProductId();
    var prodResp = storeGrpcClient.getProductById(menuId);
    if (!prodResp.getFound()) {
      throw new IllegalArgumentException("Menu item not found: " + menuId);
    }
    String storeId = prodResp.getStoreId();

    // Validate all items and compute total
    int total = 0;
    for (OrderItemDto it : items) {
      var p = storeGrpcClient.getProductById(it.getProductId());
      if (!p.getFound()) {
        throw new IllegalArgumentException("Menu item not found: " + it.getProductId());
      }
      total += (int) (p.getPrice() * it.getQuantity());
    }

    Order o = Order.builder()
        .id(java.util.UUID.randomUUID().toString())
        .userId(userId)
        .storeId(storeId)
        .totalAmount(total)
        .status(status)
        .createdAt(Instant.now())
        .isNewEntity(true)
        .build();
    Order saved = orderRepository.save(o);

    for (OrderItemDto it : items) {
      OrderItem oi = OrderItem.builder()
          .id(java.util.UUID.randomUUID().toString())
          .orderId(saved.getId())
          .menuId(it.getProductId())
          .quantity(it.getQuantity())
          .priceSnapshot(it.getPrice())
          .isNewEntity(true)
          .build();
      orderItemRepository.save(oi);
    }

    return toDto(saved, items);
  }

  public Optional<OrderDto> findById(String id) {
    return orderRepository.findById(id).map(o -> toDto(o, loadItems(o.getId())));
  }

  public java.util.List<OrderDto> findByUserId(String userId) {
    return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(order -> toDto(order, loadItems(order.getId())))
        .toList();
  }

  public java.util.List<OrderDto> findByStoreId(
      String storeId, String currentUserId, String currentUserRole) {
    validateStoreAccess(storeId, currentUserId, currentUserRole);
    return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream()
        .map(order -> toDto(order, loadItems(order.getId())))
        .toList();
  }

  @Transactional
  public OrderDto updateStatus(String orderId, String newStatus) {
    Order o = orderRepository
        .findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(o.getStatus(), Set.of());
    if (!allowed.contains(newStatus)) {
      throw new IllegalStateException(
          String.format("Cannot transition from %s to %s", o.getStatus(), newStatus));
    }

    Order updated = o.toBuilder().status(newStatus).build();
    Order saved = orderRepository.save(updated);
    return toDto(saved, loadItems(saved.getId()));
  }

  private java.util.List<OrderItemDto> loadItems(String orderId) {
    return orderItemRepository.findByOrderId(orderId).stream()
        .map(it -> new OrderItemDto(it.getMenuId(), it.getQuantity(), it.getPriceSnapshot()))
        .toList();
  }

  private void validateStoreAccess(String storeId, String currentUserId, String currentUserRole) {
    if ("ADMIN".equals(currentUserRole)) {
      return;
    }

    var storeResponse = storeGrpcClient.getStoreById(storeId);
    if (!storeResponse.getFound()) {
      throw new IllegalArgumentException("Store not found");
    }

    if (!"STORE".equals(currentUserRole) || !currentUserId.equals(storeResponse.getOwnerId())) {
      throw new IllegalStateException("해당 매장의 주문을 조회할 권한이 없습니다.");
    }
  }

  private OrderDto toDto(Order o, java.util.List<OrderItemDto> items) {
    return new OrderDto(
        o.getId(),
        o.getUserId(),
        o.getStoreId(),
        o.getTotalAmount(),
        items,
        o.getStatus(),
        o.getCreatedAt());
  }
}
