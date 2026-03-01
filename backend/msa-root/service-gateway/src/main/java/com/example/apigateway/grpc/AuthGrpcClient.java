package com.example.apigateway.grpc;

import com.example.auth.grpc.AuthGrpcServiceGrpc;
import com.example.auth.grpc.ValidateTokenRequest;
import com.example.auth.grpc.ValidateTokenResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/** service-auth gRPC 클라이언트 Gateway에서 JWT 검증 시 호출 */
@Service
public class AuthGrpcClient {

  @GrpcClient("service-auth")
  private AuthGrpcServiceGrpc.AuthGrpcServiceBlockingStub authStub;

  /**
   * JWT 토큰 검증
   *
   * @return ValidateTokenResponse (valid, userId, roles, errorMessage)
   */
  public ValidateTokenResponse validateToken(String token) {
    ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken(token).build();
    return authStub.validateToken(request);
  }
}
