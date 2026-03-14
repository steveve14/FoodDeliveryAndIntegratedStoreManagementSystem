package com.example.delivery.grpc.client;

import com.example.order.grpc.GetOrderByIdRequest;
import com.example.order.grpc.OrderGrpcServiceGrpc;
import com.example.order.grpc.OrderResponse;
import org.springframework.stereotype.Service;

/** OrderGrpcClient 타입입니다. */
@Service
public class OrderGrpcClient {

  private final OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderStub;

  public OrderGrpcClient(OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderStub) {
    this.orderStub = orderStub;
  }

  public OrderResponse getOrderById(String orderId) {
    GetOrderByIdRequest req = GetOrderByIdRequest.newBuilder().setOrderId(orderId).build();
    return orderStub.getOrderById(req);
  }
}
