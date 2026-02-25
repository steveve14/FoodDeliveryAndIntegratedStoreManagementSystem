package com.example.store.grpc;

import com.example.store.dto.StoreDto;
import com.example.store.service.StoreService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * Store 서비스 gRPC 서버 구현
 * 내부 MSA 통신용
 */
@GrpcService
public class StoreGrpcServiceImpl extends StoreGrpcServiceGrpc.StoreGrpcServiceImplBase {

    private final StoreService storeService;

    public StoreGrpcServiceImpl(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public void getStoreById(GetStoreByIdRequest request, StreamObserver<StoreResponse> responseObserver) {
        try {
            var opt = storeService.findById(request.getStoreId());
            if (opt.isPresent()) {
                StoreDto dto = opt.get();
                responseObserver.onNext(StoreResponse.newBuilder()
                        .setFound(true)
                        .setStoreId(dto.getId())
                        .setName(dto.getName())
                        .setAddress(dto.getAddress())
                        .setPhone(dto.getPhone() != null ? dto.getPhone() : "")
                        .build());
            } else {
                responseObserver.onNext(StoreResponse.newBuilder()
                        .setFound(false)
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(StoreResponse.newBuilder()
                    .setFound(false)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createStore(CreateStoreRequest request, StreamObserver<StoreResponse> responseObserver) {
        try {
            StoreDto dto = storeService.createStore(
                    request.getName(),
                    request.getAddress(),
                    request.getPhone()
            );
            responseObserver.onNext(StoreResponse.newBuilder()
                    .setFound(true)
                    .setStoreId(dto.getId())
                    .setName(dto.getName())
                    .setAddress(dto.getAddress())
                    .setPhone(dto.getPhone() != null ? dto.getPhone() : "")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(StoreResponse.newBuilder()
                    .setFound(false)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
        // 상품 조회 구현 (ProductService가 필요)
        responseObserver.onNext(ProductResponse.newBuilder()
                .setFound(false)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsByStoreId(GetProductsByStoreIdRequest request, StreamObserver<ProductListResponse> responseObserver) {
        // 상품 목록 조회 구현
        responseObserver.onNext(ProductListResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
