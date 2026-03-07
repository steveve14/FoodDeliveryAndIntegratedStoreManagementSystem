package com.example.apigateway.grpc;

import com.example.auth.grpc.AuthGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC 클라이언트 Stub 빈 등록 (Spring gRPC 공식 방식)
 *
 * <p>Gateway에서 Auth 서비스로의 JWT 검증용 gRPC 채널/스텁을 구성합니다.
 */
@Configuration
public class GrpcClientConfig {

  @Bean
  AuthGrpcServiceGrpc.AuthGrpcServiceBlockingStub authGrpcStub(GrpcChannelFactory channels) {
    return AuthGrpcServiceGrpc.newBlockingStub(channels.createChannel("service-auth"));
  }
}
