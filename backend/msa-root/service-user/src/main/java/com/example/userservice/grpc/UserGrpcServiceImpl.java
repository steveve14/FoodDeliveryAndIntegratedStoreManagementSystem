package com.example.userservice.grpc;

import com.example.userservice.dto.AuthUserDto;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/** gRPC service implementation for user operations used by internal MSA communication. */
@GrpcService
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

  private final UserService userService;

  public UserGrpcServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void authenticate(
      AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
    try {
      AuthUserDto dto = userService.authenticate(request.getEmail(), request.getPassword());

      if (dto == null) {
        responseObserver.onNext(
            AuthenticateResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage("이메일 또는 비밀번호가 올바르지 않습니다.")
                .build());
      } else {
        responseObserver.onNext(
            AuthenticateResponse.newBuilder()
                .setSuccess(true)
                .setUserId(dto.getId())
                .setEmail(dto.getEmail())
                .setName(dto.getName())
                .setRoles(dto.getRoles())
                .build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(
          AuthenticateResponse.newBuilder()
              .setSuccess(false)
              .setErrorMessage(e.getMessage())
              .build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getUserById(
      GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
    try {
      var profile = userService.getProfile(request.getUserId());
      UserDto user = userService.findById(request.getUserId());

      responseObserver.onNext(
          UserResponse.newBuilder()
              .setFound(true)
              .setUserId(profile.getId())
              .setEmail(profile.getEmail())
              .setName(profile.getName())
              .setPhone(profile.getPhone() != null ? profile.getPhone() : "")
              .setRoles(user != null ? user.getRoles() : "")
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(UserResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getUserByEmail(
      GetUserByEmailRequest request, StreamObserver<UserResponse> responseObserver) {
    try {
      UserDto dto = userService.findByEmail(request.getEmail());

      if (dto == null) {
        responseObserver.onNext(UserResponse.newBuilder().setFound(false).build());
      } else {
        responseObserver.onNext(
            UserResponse.newBuilder()
                .setFound(true)
                .setUserId(dto.getId())
                .setEmail(dto.getEmail())
                .setName(dto.getName())
                .setRoles(dto.getRoles())
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toEpochMilli() : 0)
                .build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(UserResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void createUser(
      com.example.userservice.grpc.CreateUserRequest request,
      StreamObserver<CreateUserResponse> responseObserver) {
    try {
      CreateUserRequest dto =
          new CreateUserRequest(request.getEmail(), request.getPassword(), request.getName());
      UserDto created = userService.register(dto);

      responseObserver.onNext(
          CreateUserResponse.newBuilder()
              .setSuccess(true)
              .setUserId(created.getId())
              .setEmail(created.getEmail())
              .setName(created.getName())
              .setRoles(created.getRoles())
              .build());
      responseObserver.onCompleted();
    } catch (IllegalArgumentException e) {
      responseObserver.onNext(
          CreateUserResponse.newBuilder()
              .setSuccess(false)
              .setErrorMessage(e.getMessage())
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(
          CreateUserResponse.newBuilder()
              .setSuccess(false)
              .setErrorMessage("회원가입 처리 중 오류가 발생했습니다.")
              .build());
      responseObserver.onCompleted();
    }
  }
}
