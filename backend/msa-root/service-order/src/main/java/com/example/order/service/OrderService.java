package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.grpc.client.StoreGrpcClient;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final StoreGrpcClient storeGrpcClient;

  public OrderService(
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      StoreGrpcClient storeGrpcClient) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.storeGrpcClient = storeGrpcClient;
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

    Order o =
        Order.builder()
            .id(java.util.UUID.randomUUID().toString())
            .userId(userId)
            .storeId(storeId)
            .totalAmount(total)
            .status(status)
            .createdAt(Instant.now())
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
              .build();
      orderItemRepository.save(oi);
    }

    return toDto(saved, items);
  }

  public Optional<OrderDto> findById(String id) {
    return orderRepository.findById(id).map(o -> toDto(o, loadItems(o.getId())));
  }

  @Transactional
  public OrderDto updateStatus(String orderId, String newStatus) {
    Order o =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    Order updated = o.toBuilder().status(newStatus).build();
    Order saved = orderRepository.save(updated);
    return toDto(saved, loadItems(saved.getId()));
  }

  private java.util.List<OrderItemDto> loadItems(String orderId) {
    return orderItemRepository.findByOrderId(orderId).stream()
        .map(it -> new OrderItemDto(it.getMenuId(), it.getQuantity(), it.getPriceSnapshot()))
        .toList();
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
