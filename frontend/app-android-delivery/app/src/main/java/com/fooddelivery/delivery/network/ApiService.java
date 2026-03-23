package com.fooddelivery.delivery.network;

import com.fooddelivery.delivery.models.ApiResponse;
import com.fooddelivery.delivery.models.LoginRequest;
import com.fooddelivery.delivery.models.OrderDto;
import com.fooddelivery.delivery.models.OrderStatusRequest;
import com.fooddelivery.delivery.models.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/v1/auth/login")
    Call<ApiResponse<UserDto>> login(@Body LoginRequest request);

    @GET("api/v1/orders")
    Call<ApiResponse<List<OrderDto>>> getAllOrders();

    @PATCH("api/v1/orders/{id}/status")
    Call<ApiResponse<OrderDto>> updateOrderStatus(@Path("id") String orderId, @Body OrderStatusRequest request);
}
