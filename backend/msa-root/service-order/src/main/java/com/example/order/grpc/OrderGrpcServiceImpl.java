package com.example.order.grpc;

import com.example.order.dto.OrderDto;
import com.example.order.service.OrderService;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/*
 * Order 서비스 gRPC 서버 구현
 * 내부 MSA 통신용
 */
@GrpcService
public class OrderGrpcServiceImpl extends OrderGrpcServiceGrpc.OrderGrpcServiceImplBase {

  private final OrderService orderService;

  public OrderGrpcServiceImpl(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public void getOrderById(
      GetOrderByIdRequest request, StreamObserver<OrderResponse> responseObserver) {
    try {
      var opt = orderService.findById(request.getOrderId());
      if (opt.isPresent()) {
        OrderDto dto = opt.get();
        responseObserver.onNext(
            OrderResponse.newBuilder()
                .setFound(true)
                .setOrderId(dto.getId())
                .setUserId(dto.getUserId())
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toEpochMilli() : 0)
                .build());
      } else {
        responseObserver.onNext(OrderResponse.newBuilder().setFound(false).build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(OrderResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void createOrder(
      CreateOrderGrpcRequest request, StreamObserver<OrderResponse> responseObserver) {
    try {
      // Build item list from proto CreateOrderGrpcRequest
      java.util.List<com.example.order.dto.OrderItemDto> items = new java.util.ArrayList<>();
      for (var it : request.getItemsList()) {
        items.add(
            new com.example.order.dto.OrderItemDto(
                it.getProductId(), it.getQuantity(), it.getPrice()));
      }
      OrderDto dto = orderService.createOrder(request.getUserId(), items, "CREATED");
      responseObserver.onNext(
          OrderResponse.newBuilder()
              .setFound(true)
              .setOrderId(dto.getId())
              .setUserId(dto.getUserId())
              .setStatus(dto.getStatus())
              .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toEpochMilli() : 0)
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(OrderResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getOrdersByUserId(
      GetOrdersByUserIdRequest request, StreamObserver<OrderListResponse> responseObserver) {
    // 사용자 ID로 주문 목록 조회 - 구현 필요
    responseObserver.onNext(OrderListResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void updateOrderStatus(
      UpdateOrderStatusRequest request, StreamObserver<OrderResponse> responseObserver) {
    // 주문 상태 업데이트 - 구현 필요
    responseObserver.onNext(OrderResponse.newBuilder().setFound(false).build());
    responseObserver.onCompleted();
  }
}
