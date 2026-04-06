package com.fooddelivery.delivery.network;

import com.fooddelivery.delivery.models.ApiResponse;
import com.fooddelivery.delivery.models.DeliveryDto;
import com.fooddelivery.delivery.models.LoginRequest;
import com.fooddelivery.delivery.models.OrderDto;
import com.fooddelivery.delivery.models.OrderStatusRequest;
import com.fooddelivery.delivery.models.UserDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/v1/auth/login")
    Call<ApiResponse<UserDto>> login(@Body LoginRequest request);

    // 배달 전용 엔드포인트
    @GET("api/v1/deliveries/{id}")
    Call<ApiResponse<DeliveryDto>> getDelivery(@Path("id") String id);

    @PATCH("api/v1/deliveries/{id}/status")
    Call<ApiResponse<DeliveryDto>> updateDeliveryStatus(@Path("id") String id, @Body Map<String, String> body);

    // 주문 조회 (COOKING/DELIVERING 상태 필터용)
    @GET("api/v1/orders")
    Call<ApiResponse<List<OrderDto>>> getAllOrders();

    @GET("api/v1/orders/{id}")
    Call<ApiResponse<OrderDto>> getOrder(@Path("id") String id);

    @PATCH("api/v1/orders/{id}/status")
    Call<ApiResponse<OrderDto>> updateOrderStatus(@Path("id") String orderId, @Body OrderStatusRequest request);
}
