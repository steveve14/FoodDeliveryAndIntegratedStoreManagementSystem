package com.example.apigateway.grpc;

import com.example.auth.grpc.AuthGrpcServiceGrpc;
import com.example.auth.grpc.ValidateTokenRequest;
import com.example.auth.grpc.ValidateTokenResponse;
import org.springframework.stereotype.Service;

/** AuthGrpcClient 타입입니다. */
@Service
public class AuthGrpcClient {

  private final AuthGrpcServiceGrpc.AuthGrpcServiceBlockingStub authStub;

  /** 인증 서비스 gRPC 클라이언트를 생성합니다. */
  public AuthGrpcClient(AuthGrpcServiceGrpc.AuthGrpcServiceBlockingStub authStub) {
    this.authStub = authStub;
  }

  /**
   * JWT 토큰을 검증합니다.
   *
   * @return ValidateTokenResponse (valid, userId, roles, errorMessage)
   */
  public ValidateTokenResponse validateToken(String token) {
    ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken(token).build();
    return authStub.validateToken(request);
  }
}
