package com.example.delivery.service;

import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.entity.Delivery;
import com.example.delivery.grpc.client.OrderGrpcClient;
import com.example.delivery.repository.DeliveryRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final OrderGrpcClient orderGrpcClient;

  // 상태 전이 규칙: SCHEDULED → PICKED_UP → COMPLETED
  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS =
      Map.of(
          "SCHEDULED", Set.of("PICKED_UP"),
          "PICKED_UP", Set.of("COMPLETED"));

  // MVP: 고정 배달비 3,000원
  private static final int FIXED_DELIVERY_FEE = 3000;

  public DeliveryService(DeliveryRepository deliveryRepository, OrderGrpcClient orderGrpcClient) {
    this.deliveryRepository = deliveryRepository;
    this.orderGrpcClient = orderGrpcClient;
  }

  @Transactional
  public DeliveryDto createDelivery(String orderId, String courier, String status) {
    var resp = orderGrpcClient.getOrderById(orderId);
    if (!resp.getFound()) {
      throw new IllegalArgumentException("Order not found: " + orderId);
    }

    String assigned = (courier != null && !courier.isBlank()) ? courier : "unassigned";

    Delivery d =
        Delivery.builder()
            .id(UUID.randomUUID().toString())
            .orderId(orderId)
            .courier(assigned)
            .status(status)
            .deliveryFee(FIXED_DELIVERY_FEE)
            .scheduledAt(Instant.now())
            .build();
    Delivery saved = deliveryRepository.save(d);
    return toDto(saved);
  }

  public Optional<DeliveryDto> findById(String id) {
    return deliveryRepository.findById(id).map(this::toDto);
  }

  @Transactional
  public DeliveryDto updateStatus(String deliveryId, String newStatus) {
    Delivery d =
        deliveryRepository
            .findById(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));

    Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(d.getStatus(), Set.of());
    if (!allowed.contains(newStatus)) {
      throw new IllegalStateException(
          String.format("Cannot transition from %s to %s", d.getStatus(), newStatus));
    }

    Delivery updated = d.toBuilder().status(newStatus).build();
    Delivery saved = deliveryRepository.save(updated);
    return toDto(saved);
  }

  private DeliveryDto toDto(Delivery d) {
    return new DeliveryDto(
        d.getId(),
        d.getOrderId(),
        d.getCourier(),
        d.getStatus(),
        d.getDeliveryFee(),
        d.getScheduledAt());
  }
}
