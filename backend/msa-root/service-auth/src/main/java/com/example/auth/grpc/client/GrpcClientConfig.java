package com.example.auth.grpc.client;

import com.example.userservice.grpc.UserGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/** gRPC 클라이언트 스텁 빈을 등록합니다. */
@Configuration
public class GrpcClientConfig {

  /** service-user 채널 기반 블로킹 스텁을 생성합니다. */
  @Bean
  UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcStub(GrpcChannelFactory channels) {
    return UserGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-user"));
  }
}
