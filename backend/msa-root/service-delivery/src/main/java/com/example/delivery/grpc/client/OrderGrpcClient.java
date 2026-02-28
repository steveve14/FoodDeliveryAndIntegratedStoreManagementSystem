package com.example.delivery.grpc.client;

import com.example.order.grpc.GetOrderByIdRequest;
import com.example.order.grpc.OrderGrpcServiceGrpc;
import com.example.order.grpc.OrderResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class OrderGrpcClient {

  @GrpcClient("service-order")
  private OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderStub;

  public OrderResponse getOrderById(String orderId) {
    GetOrderByIdRequest req = GetOrderByIdRequest.newBuilder().setOrderId(orderId).build();
    return orderStub.getOrderById(req);
  }
}
