package com.example.order.grpc.client;

import com.example.store.grpc.GetProductByIdRequest;
import com.example.store.grpc.GetStoreByIdRequest;
import com.example.store.grpc.ProductResponse;
import com.example.store.grpc.StoreResponse;
import com.example.store.grpc.StoreGrpcServiceGrpc;
import org.springframework.stereotype.Service;

/** service-store gRPC 클라이언트 service-order에서 상품 정보 조회 시 호출 */
@Service
public class StoreGrpcClient {

  private final StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeStub;

  public StoreGrpcClient(StoreGrpcServiceGrpc.StoreGrpcServiceBlockingStub storeStub) {
    this.storeStub = storeStub;
  }

  /** 상품 ID로 상품 정보 조회 */
  public ProductResponse getProductById(String productId) {
    GetProductByIdRequest request = GetProductByIdRequest.newBuilder().setProductId(productId).build();
    return storeStub.getProductById(request);
  }

  public StoreResponse getStoreById(String storeId) {
    GetStoreByIdRequest request = GetStoreByIdRequest.newBuilder().setStoreId(storeId).build();
    return storeStub.getStoreById(request);
  }
}
