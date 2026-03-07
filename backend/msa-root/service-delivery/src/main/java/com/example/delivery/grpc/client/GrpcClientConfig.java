package com.example.delivery.grpc.client;

import com.example.order.grpc.OrderGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC 클라이언트 Stub 빈 등록 (Spring gRPC 공식 방식)
 *
 * <p>Delivery 서비스에서 Order 서비스의 주문 정보를 조회하기 위한 gRPC 스텁입니다.
 */
@Configuration
public class GrpcClientConfig {

  @Bean
  OrderGrpcServiceGrpc.OrderGrpcServiceBlockingStub orderGrpcStub(GrpcChannelFactory channels) {
    return OrderGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-order"));
  }
}
