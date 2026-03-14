package com.example.delivery.grpc.client;

import com.example.order.grpc.GetOrderByIdRequest;
import com.example.order.grpc.OrderGrpcServiceGrpc;
import com.example.order.grpc.OrderResponse;
import org.springframework.stereotype.Service;

/** OrderGrpcClient 타입입니다. */
@Service
public class OrderGrpcClient {

  private final OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderStub;

  /** gRPC 블로킹 스텁을 주입받습니다. */
  public OrderGrpcClient(OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderStub) {
    this.orderStub = orderStub;
  }

  /** 주문 ID로 주문 정보를 조회합니다. */
  public OrderResponse getOrderById(String orderId) {
    GetOrderByIdRequest req = GetOrderByIdRequest.newBuilder().setOrderId(orderId).build();
    return orderStub.getOrderById(req);
  }
}
