package com.example.order.service;

import com.example.order.dto.FrontendCustomerOrderSummaryDto;
import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.grpc.client.StoreGrpcClient;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** OrderService 타입입니다. */
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final StoreGrpcClient storeGrpcClient;
  private final JdbcTemplate jdbcTemplate;

  @Value("${app.order.vip-threshold:10}")
  private long vipThreshold = 10L;

  // 주문 상태 전이 규칙: CREATED → COOKING → DELIVERING → DONE | CANCELLED
  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS =
      Map.of(
          "CREATED", Set.of("COOKING", "CANCELLED"),
          "COOKING", Set.of("DELIVERING", "CANCELLED"),
          "DELIVERING", Set.of("DONE", "CANCELLED"));

  /** 주문 서비스 인스턴스를 생성합니다. */
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

  /** 고객 주문 요약 목록을 조회합니다. */
  public java.util.List<FrontendCustomerOrderSummaryDto> getFrontendCustomerSummaries(
      String currentUserId, String currentUserRole) {
    if ("ADMIN".equals(currentUserRole)) {
      return getGlobalCustomerSummaries();
    }

    if (!"STORE".equals(currentUserRole)) {
      throw new IllegalStateException("고객 요약 목록을 조회할 권한이 없습니다.");
    }

    return getStoreScopedCustomerSummaries(currentUserId);
  }

  private java.util.List<FrontendCustomerOrderSummaryDto> getGlobalCustomerSummaries() {
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
                rs.getLong("orders_count") >= vipThreshold ? "vip" : "regular"));
  }

  private java.util.List<FrontendCustomerOrderSummaryDto> getStoreScopedCustomerSummaries(
      String currentUserId) {
    java.util.List<StoreScopedCustomerSummaryRow> rows =
        jdbcTemplate.query(
            """
            SELECT user_id, store_id, COUNT(*) AS orders_count, MAX(created_at) AS last_order_at
            FROM orders
            WHERE user_id IS NOT NULL AND store_id IS NOT NULL
            GROUP BY user_id, store_id
            ORDER BY last_order_at DESC
            """,
            (rs, rowNum) ->
                new StoreScopedCustomerSummaryRow(
                    rs.getString("user_id"),
                    rs.getString("store_id"),
                    rs.getLong("orders_count"),
                    rs.getTimestamp("last_order_at").toInstant()));

    Map<String, Boolean> ownedStoreCache = new HashMap<>();
    Map<String, CustomerSummaryAccumulator> mergedByUser = new HashMap<>();

    for (StoreScopedCustomerSummaryRow row : rows) {
      boolean ownedStore =
          ownedStoreCache.computeIfAbsent(
              row.storeId(), key -> isStoreOwnedByUser(key, currentUserId));

      if (!ownedStore) {
        continue;
      }

      mergedByUser
          .computeIfAbsent(row.userId(), key -> new CustomerSummaryAccumulator())
          .merge(row.ordersCount(), row.lastOrderAt());
    }

    java.util.List<FrontendCustomerOrderSummaryDto> result = new ArrayList<>();
    for (Map.Entry<String, CustomerSummaryAccumulator> entry : mergedByUser.entrySet()) {
      CustomerSummaryAccumulator acc = entry.getValue();
      result.add(
          new FrontendCustomerOrderSummaryDto(
              entry.getKey(),
              acc.ordersCount,
              acc.lastOrderAt,
              acc.ordersCount >= vipThreshold ? "vip" : "regular"));
    }

    result.sort(
        Comparator.comparing(FrontendCustomerOrderSummaryDto::getLastOrderAt).reversed());
    return result;
  }

  /** 주문을 생성합니다. */
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

    Order o =
        Order.builder()
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
      OrderItem oi =
          OrderItem.builder()
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

  /** ID로 주문을 조회합니다. */
  public Optional<OrderDto> findById(String id) {
    return orderRepository.findById(id).map(o -> toDto(o, loadItems(o.getId())));
  }

  /** 사용자 ID 기준으로 주문 목록을 조회합니다. */
  public java.util.List<OrderDto> findByUserId(String userId) {
    return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(order -> toDto(order, loadItems(order.getId())))
        .toList();
  }

  /** 매장 ID 기준으로 주문 목록을 조회합니다. */
  public java.util.List<OrderDto> findByStoreId(
      String storeId, String currentUserId, String currentUserRole) {
    validateStoreAccess(storeId, currentUserId, currentUserRole);
    return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream()
        .map(order -> toDto(order, loadItems(order.getId())))
        .toList();
  }

  /** 현재 사용자가 주문 상세를 조회할 수 있는지 검증합니다. */
  public boolean canAccessOrder(OrderDto order, String currentUserId, String currentUserRole) {
    if ("ADMIN".equals(currentUserRole)) {
      return true;
    }
    if ("USER".equals(currentUserRole)) {
      return currentUserId != null && currentUserId.equals(order.getUserId());
    }
    if (!"STORE".equals(currentUserRole)) {
      return false;
    }
    try {
      validateStoreAccess(order.getStoreId(), currentUserId, currentUserRole);
      return true;
    } catch (RuntimeException ex) {
      return false;
    }
  }

  /** 주문 상태를 변경합니다. */
  @Transactional
  public OrderDto updateStatus(String orderId, String newStatus) {
    Order o =
        orderRepository
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

  private boolean isStoreOwnedByUser(String storeId, String currentUserId) {
    var storeResponse = storeGrpcClient.getStoreById(storeId);
    return storeResponse.getFound() && currentUserId.equals(storeResponse.getOwnerId());
  }

  private static class CustomerSummaryAccumulator {
    private long ordersCount;
    private Instant lastOrderAt;

    private void merge(long count, Instant candidateLastOrderAt) {
      this.ordersCount += count;
      if (this.lastOrderAt == null || candidateLastOrderAt.isAfter(this.lastOrderAt)) {
        this.lastOrderAt = candidateLastOrderAt;
      }
    }
  }

  private record StoreScopedCustomerSummaryRow(
      String userId, String storeId, long ordersCount, Instant lastOrderAt) {}

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
