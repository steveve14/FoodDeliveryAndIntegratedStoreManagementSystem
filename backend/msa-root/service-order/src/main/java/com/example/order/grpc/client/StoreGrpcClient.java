package com.example.order.grpc.client;

import com.example.store.grpc.GetProductByIdRequest;
import com.example.store.grpc.ProductResponse;
import com.example.store.grpc.StoreGrpcServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * service-store gRPC 클라이언트
 * service-order에서 상품 정보 조회 시 호출
 */
@Service
public class StoreGrpcClient {

    @GrpcClient("service-store")
    private StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeStub;

    /**
     * 상품 ID로 상품 정보 조회
     */
    public ProductResponse getProductById(String productId) {
        GetProductByIdRequest request = GetProductByIdRequest.newBuilder()
                .setProductId(productId)
                .build();
        return storeStub.getProductById(request);
    }
}
