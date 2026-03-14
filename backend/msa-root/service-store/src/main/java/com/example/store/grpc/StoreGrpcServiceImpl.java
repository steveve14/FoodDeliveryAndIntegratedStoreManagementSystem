package com.example.store.grpc;

import com.example.store.dto.StoreDto;
import com.example.store.entity.Menu;
import com.example.store.repository.MenuRepository;
import com.example.store.service.StoreService;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/** StoreGrpcServiceImpl 타입입니다. */
@GrpcService
public class StoreGrpcServiceImpl extends StoreGrpcServiceGrpc.StoreGrpcServiceImplBase {

  private final StoreService storeService;
  private final MenuRepository menuRepository;

  /** 가게 gRPC 서비스 구현을 생성합니다. */
  public StoreGrpcServiceImpl(StoreService storeService, MenuRepository menuRepository) {
    this.storeService = storeService;
    this.menuRepository = menuRepository;
  }

  @Override
  public void getStoreById(
      GetStoreByIdRequest request, StreamObserver<StoreResponse> responseObserver) {
    try {
      var opt = storeService.findById(request.getStoreId());
      if (opt.isPresent()) {
        StoreDto dto = opt.get();
        responseObserver.onNext(
            StoreResponse.newBuilder()
                .setFound(true)
                .setStoreId(dto.getId())
                .setName(dto.getName())
                .setAddress(dto.getAddress())
                .setPhone(dto.getPhone() != null ? dto.getPhone() : "")
                .setCategory(dto.getCategory() != null ? dto.getCategory() : "")
                .setStatus(dto.getStatus() != null ? dto.getStatus() : "")
                .setLatitude(dto.getLatitude() != null ? dto.getLatitude() : 0)
                .setLongitude(dto.getLongitude() != null ? dto.getLongitude() : 0)
                .setMinOrderAmount(dto.getMinOrderAmount() != null ? dto.getMinOrderAmount() : 0)
                .setRatingAvg(dto.getRatingAvg() != null ? dto.getRatingAvg() : 0)
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
                .setOpeningHours(dto.getOpeningHours() != null ? dto.getOpeningHours() : "")
                .setOwnerId(dto.getOwnerId() != null ? dto.getOwnerId() : "")
                .build());
      } else {
        responseObserver.onNext(StoreResponse.newBuilder().setFound(false).build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(StoreResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void createStore(
      CreateStoreRequest request, StreamObserver<StoreResponse> responseObserver) {
    try {
      com.example.store.dto.CreateStoreRequest req =
          new com.example.store.dto.CreateStoreRequest(
              request.getName(),
              request.getAddress(),
              request.getPhone(),
              request.getCategory(),
              request.getStatus(),
              request.getLatitude(),
              request.getLongitude(),
              request.getMinOrderAmount(),
              request.getRatingAvg(),
              request.getDescription(),
              request.getOpeningHours(),
              request.getOwnerId());
      StoreDto dto = storeService.createStore(req);
      responseObserver.onNext(
          StoreResponse.newBuilder()
              .setFound(true)
              .setStoreId(dto.getId())
              .setName(dto.getName())
              .setAddress(dto.getAddress())
              .setPhone(dto.getPhone() != null ? dto.getPhone() : "")
              .build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(StoreResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getProductById(
      GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
    try {
      String productId = request.getProductId();
      var opt = menuRepository.findById(productId);
      if (opt.isPresent()) {
        Menu m = opt.get();
        responseObserver.onNext(
            ProductResponse.newBuilder()
                .setFound(true)
                .setProductId(m.getId())
                .setStoreId(m.getStoreId())
                .setName(m.getName())
                .setDescription(m.getDescription() != null ? m.getDescription() : "")
                .setPrice(m.getPrice())
                .build());
      } else {
        responseObserver.onNext(ProductResponse.newBuilder().setFound(false).build());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(ProductResponse.newBuilder().setFound(false).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void getProductsByStoreId(
      GetProductsByStoreIdRequest request, StreamObserver<ProductListResponse> responseObserver) {
    try {
      String storeId = request.getStoreId();
      var list = menuRepository.findByStoreId(storeId);
      var builder = ProductListResponse.newBuilder();
      for (Menu m : list) {
        builder.addProducts(
            ProductResponse.newBuilder()
                .setFound(true)
                .setProductId(m.getId())
                .setStoreId(m.getStoreId())
                .setName(m.getName())
                .setDescription(m.getDescription() != null ? m.getDescription() : "")
                .setPrice(m.getPrice()));
      }
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onNext(ProductListResponse.newBuilder().build());
      responseObserver.onCompleted();
    }
  }
}
