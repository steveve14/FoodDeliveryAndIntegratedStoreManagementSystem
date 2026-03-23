package com.fooddelivery.user.network;

import com.fooddelivery.user.models.ApiResponse;
import com.fooddelivery.user.models.CreateOrderRequest;
import com.fooddelivery.user.models.LoginRequest;
import com.fooddelivery.user.models.MenuDto;
import com.fooddelivery.user.models.OrderDto;
import com.fooddelivery.user.models.StoreDto;
import com.fooddelivery.user.models.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/v1/auth/login")
    Call<ApiResponse<UserDto>> login(@Body LoginRequest request);

    @GET("api/v1/stores")
    Call<ApiResponse<List<StoreDto>>> getStores(
            @Query("category") String category,
            @Query("status") String status
    );

    @GET("api/v1/stores/{id}")
    Call<ApiResponse<StoreDto>> getStore(@Path("id") String id);

    @GET("api/v1/stores/{storeId}/menus")
    Call<ApiResponse<List<MenuDto>>> getMenus(@Path("storeId") String storeId);

    @POST("api/v1/orders")
    Call<ApiResponse<OrderDto>> createOrder(@Body CreateOrderRequest request);

    @GET("api/v1/orders/my")
    Call<ApiResponse<List<OrderDto>>> getMyOrders();
}
