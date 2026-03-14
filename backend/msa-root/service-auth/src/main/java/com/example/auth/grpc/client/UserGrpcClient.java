package com.example.auth.grpc.client;

import com.example.userservice.grpc.AuthenticateRequest;
import com.example.userservice.grpc.AuthenticateResponse;
import com.example.userservice.grpc.UserGrpcServiceGrpc;
import org.springframework.stereotype.Service;

/** service-user gRPC 클라이언트입니다. service-auth에서 사용자 인증 시 호출합니다. */
@Service
public class UserGrpcClient {

  private final UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;

  /** gRPC 블로킹 스텁을 주입받습니다. */
  public UserGrpcClient(UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub) {
    this.userStub = userStub;
  }

  /**
   * 이메일/비밀번호로 사용자 인증합니다.
   *
   * @return AuthenticateResponse (success, userId, email, name, roles, errorMessage)
   */
  public AuthenticateResponse authenticate(String email, String password) {
    AuthenticateRequest request =
        AuthenticateRequest.newBuilder().setEmail(email).setPassword(password).build();
    return userStub.authenticate(request);
  }

  /** 이메일로 사용자 정보를 조회합니다. */
  public com.example.userservice.grpc.UserResponse getUserByEmail(String email) {
    com.example.userservice.grpc.GetUserByEmailRequest req =
        com.example.userservice.grpc.GetUserByEmailRequest.newBuilder().setEmail(email).build();
    return userStub.getUserByEmail(req);
  }

  /** 사용자 ID로 사용자 정보를 조회합니다. */
  public com.example.userservice.grpc.UserResponse getUserById(String userId) {
    com.example.userservice.grpc.GetUserByIdRequest req =
        com.example.userservice.grpc.GetUserByIdRequest.newBuilder().setUserId(userId).build();
    return userStub.getUserById(req);
  }

  /** 사용자를 생성합니다. */
  public com.example.userservice.grpc.CreateUserResponse createUser(
      String email, String password, String name) {
    com.example.userservice.grpc.CreateUserRequest req =
        com.example.userservice.grpc.CreateUserRequest.newBuilder()
            .setEmail(email)
            .setPassword(password)
            .setName(name)
            .build();
    return userStub.createUser(req);
  }
}
