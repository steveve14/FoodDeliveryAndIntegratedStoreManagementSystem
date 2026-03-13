package com.example.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.delivery.entity.Delivery;
import com.example.delivery.grpc.client.OrderGrpcClient;
import com.example.delivery.repository.DeliveryRepository;
import com.example.order.grpc.OrderResponse;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

  @Mock private DeliveryRepository deliveryRepository;
  @Mock private OrderGrpcClient orderGrpcClient;

  @InjectMocks private DeliveryService deliveryService;

  @Test
  void createDeliveryUsesDefaultCourierAndFixedFee() {
    when(orderGrpcClient.getOrderById("order-1"))
        .thenReturn(OrderResponse.newBuilder().setFound(true).setOrderId("order-1").build());
    when(deliveryRepository.save(any(Delivery.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, Delivery.class));

    var result = deliveryService.createDelivery("order-1", "", "SCHEDULED");

    assertThat(result.getOrderId()).isEqualTo("order-1");
    assertThat(result.getCourier()).isEqualTo("unassigned");
    assertThat(result.getStatus()).isEqualTo("SCHEDULED");
    assertThat(result.getDeliveryFee()).isEqualTo(3000);

    ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
    verify(deliveryRepository).save(captor.capture());
    assertThat(captor.getValue().getScheduledAt()).isNotNull();
  }

  @Test
  void createDeliveryFailsWhenOrderDoesNotExist() {
    when(orderGrpcClient.getOrderById("missing"))
        .thenReturn(OrderResponse.newBuilder().setFound(false).build());

    assertThatThrownBy(() -> deliveryService.createDelivery("missing", "courier-a", "SCHEDULED"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order not found");
  }

  @Test
  void updateStatusAllowsConfiguredTransition() {
    Delivery existing =
        Delivery.builder()
            .id("delivery-1")
            .orderId("order-1")
            .courier("courier-a")
            .status("SCHEDULED")
            .deliveryFee(3000)
            .scheduledAt(Instant.now())
            .isNewEntity(false)
            .build();

    when(deliveryRepository.findById("delivery-1")).thenReturn(Optional.of(existing));
    when(deliveryRepository.save(any(Delivery.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, Delivery.class));

    var result = deliveryService.updateStatus("delivery-1", "IN_PROGRESS");

    assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
  }

  @Test
  void updateStatusRejectsInvalidTransition() {
    Delivery existing =
        Delivery.builder()
            .id("delivery-1")
            .orderId("order-1")
            .courier("courier-a")
            .status("SCHEDULED")
            .deliveryFee(3000)
            .scheduledAt(Instant.now())
            .isNewEntity(false)
            .build();

    when(deliveryRepository.findById("delivery-1")).thenReturn(Optional.of(existing));

    assertThatThrownBy(() -> deliveryService.updateStatus("delivery-1", "DELIVERED"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot transition");
  }
}
