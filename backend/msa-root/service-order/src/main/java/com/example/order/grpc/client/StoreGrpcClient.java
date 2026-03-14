package com.example.order.grpc.client;

import com.example.store.grpc.GetProductByIdRequest;
import com.example.store.grpc.GetStoreByIdRequest;
import com.example.store.grpc.ProductResponse;
import com.example.store.grpc.StoreGrpcServiceGrpc;
import com.example.store.grpc.StoreResponse;
import org.springframework.stereotype.Service;

/** StoreGrpcClient 타입입니다. */
@Service
public class StoreGrpcClient {

  private final StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeStub;

  /** 매장 서비스 gRPC 클라이언트를 생성합니다. */
  public StoreGrpcClient(StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeStub) {
    this.storeStub = storeStub;
  }

  /** 상품 ID로 상품 정보를 조회합니다. */
  public ProductResponse getProductById(String productId) {
    GetProductByIdRequest request =
        GetProductByIdRequest.newBuilder().setProductId(productId).build();
    return storeStub.getProductById(request);
  }

  /** 매장 ID로 매장 정보를 조회합니다. */
  public StoreResponse getStoreById(String storeId) {
    GetStoreByIdRequest request = GetStoreByIdRequest.newBuilder().setStoreId(storeId).build();
    return storeStub.getStoreById(request);
  }
}
