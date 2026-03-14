package com.example.auth.grpc;

import com.example.auth.security.JwtProvider;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/*
 * Auth 서비스 gRPC 서버 구현
 * 다른 MSA에서 JWT 검증 시 호출
 */
/** AuthGrpcServiceImpl 타입입니다. */
@GrpcService
public class AuthGrpcServiceImpl extends AuthGrpcServiceGrpc.AuthGrpcServiceImplBase {

  private final JwtProvider jwtProvider;

  /** gRPC 서비스 구현에 사용할 JWT 유틸을 주입합니다. */
  public AuthGrpcServiceImpl(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  public void validateToken(
      ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
    try {
      String token = request.getToken();

      if (!jwtProvider.validateToken(token)) {
        responseObserver.onNext(
            ValidateTokenResponse.newBuilder()
                .setValid(false)
                .setErrorMessage("Invalid or expired token")
                .build());
        responseObserver.onCompleted();
        return;
      }

      String userId = jwtProvider.getUserIdFromToken(token);
      String roles = jwtProvider.getRolesFromToken(token);

      responseObserver.onNext(
          ValidateTokenResponse.newBuilder()
              .setValid(true)
              .setUserId(userId)
              .setRoles(roles != null ? roles : "")
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(
          ValidateTokenResponse.newBuilder()
              .setValid(false)
              .setErrorMessage(e.getMessage())
              .build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void refreshToken(
      RefreshTokenRequest request, StreamObserver<TokenPairResponse> responseObserver) {
    try {
      String refreshToken = request.getRefreshToken();

      if (!jwtProvider.validateToken(refreshToken)) {
        responseObserver.onNext(
            TokenPairResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage("Invalid or expired refresh token")
                .build());
        responseObserver.onCompleted();
        return;
      }

      String userId = jwtProvider.getUserIdFromToken(refreshToken);
      String roles = jwtProvider.getRolesFromToken(refreshToken);

      String newAccessToken = jwtProvider.createAccessToken(userId, roles);
      String newRefreshToken = jwtProvider.createRefreshToken(userId, roles);

      responseObserver.onNext(
          TokenPairResponse.newBuilder()
              .setSuccess(true)
              .setAccessToken(newAccessToken)
              .setRefreshToken(newRefreshToken)
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(
          TokenPairResponse.newBuilder().setSuccess(false).setErrorMessage(e.getMessage()).build());
      responseObserver.onCompleted();
    }
  }
}
