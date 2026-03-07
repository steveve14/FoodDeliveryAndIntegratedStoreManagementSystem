package com.example.order.grpc.client;

import com.example.store.grpc.StoreGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC 클라이언트 Stub 빈 등록 (Spring gRPC 공식 방식)
 *
 * <p>Order 서비스에서 Store 서비스의 상품 정보를 조회하기 위한 gRPC 스텁입니다.
 */
@Configuration
public class GrpcClientConfig {

  @Bean
  StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeGrpcStub(GrpcChannelFactory channels) {
    return StoreGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-store"));
  }
}
