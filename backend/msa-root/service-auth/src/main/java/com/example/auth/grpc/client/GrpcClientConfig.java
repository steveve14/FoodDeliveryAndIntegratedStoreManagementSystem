package com.example.auth.grpc.client;

import com.example.userservice.grpc.UserGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC 클라이언트 Stub 빈 등록 (Spring gRPC 공식 방식)
 *
 * <p>GrpcChannelFactory를 통해 채널을 생성하고 BlockingStub 빈을 등록합니다. 채널 이름은 application.yml의
 * spring.grpc.client.channels.<name> 과 일치해야 합니다.
 */
@Configuration
public class GrpcClientConfig {

  @Bean
  UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcStub(GrpcChannelFactory channels) {
    return UserGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-user"));
  }
}
