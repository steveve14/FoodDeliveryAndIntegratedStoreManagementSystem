package com.example.delivery.grpc;

import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.service.DeliveryService;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/*
 * Delivery 서비스 gRPC 서버 구현
 * 내부 MSA 통신용
 */
/** DeliveryGrpcServiceImpl 타입입니다. */
@GrpcService
public class DeliveryGrpcServiceImpl extends DeliveryGrpcServiceGrpc.DeliveryGrpcServiceImplBase {

  private final DeliveryService deliveryService;

  public DeliveryGrpcServiceImpl(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  @Override
  public void getDeliveryById(
      GetDeliveryByIdRequest request, StreamObserver<DeliveryResponse> responseObserver) {
    try {
      var opt = deliveryService.findById(request.getDeliveryId());
      if (opt.isPresent()) {
        DeliveryDto dto = opt.get();
        responseObserver.onNext(
            DeliveryResponse.newBuilder()
                .setFound(true)
                .setDeliveryId(dto.getId())
                .setOrderId(dto.getOrderId())
                .setCourier(dto.getCourier() != null ? dto.getCourier() : "")
                .setStatus(dto.getStatus() != null ? dto.getStatus() : "")
                .setScheduledAt(
                    dto.getScheduledAt() != null ? dto.getScheduledAt().toEpochMilli() : 0)
                .build());
      } else {
        responseObserver.onNext(DeliveryResponse.newBuilder().setFound(false).build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(DeliveryResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void createDelivery(
      CreateDeliveryGrpcRequest request, StreamObserver<DeliveryResponse> responseObserver) {
    try {
      DeliveryDto dto =
          deliveryService.createDelivery(request.getOrderId(), request.getCourier(), "SCHEDULED");
      responseObserver.onNext(
          DeliveryResponse.newBuilder()
              .setFound(true)
              .setDeliveryId(dto.getId())
              .setOrderId(dto.getOrderId())
              .setCourier(dto.getCourier() != null ? dto.getCourier() : "")
              .setStatus(dto.getStatus() != null ? dto.getStatus() : "")
              .setScheduledAt(
                  dto.getScheduledAt() != null ? dto.getScheduledAt().toEpochMilli() : 0)
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(DeliveryResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getDeliveryByOrderId(
      GetDeliveryByOrderIdRequest request, StreamObserver<DeliveryResponse> responseObserver) {
    // 주문 ID로 배달 조회 - 구현 필요
    responseObserver.onNext(DeliveryResponse.newBuilder().setFound(false).build());
    responseObserver.onCompleted();
  }

  @Override
  public void updateDeliveryStatus(
      UpdateDeliveryStatusRequest request, StreamObserver<DeliveryResponse> responseObserver) {
    try {
      DeliveryDto dto = deliveryService.updateStatus(request.getDeliveryId(), request.getStatus());
      responseObserver.onNext(
          DeliveryResponse.newBuilder()
              .setFound(true)
              .setDeliveryId(dto.getId())
              .setOrderId(dto.getOrderId())
              .setCourier(dto.getCourier() != null ? dto.getCourier() : "")
              .setStatus(dto.getStatus())
              .setScheduledAt(
                  dto.getScheduledAt() != null ? dto.getScheduledAt().toEpochMilli() : 0)
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(DeliveryResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }
}
