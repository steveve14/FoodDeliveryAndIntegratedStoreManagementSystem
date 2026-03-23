package com.fooddelivery.kiosk.network;

import com.fooddelivery.kiosk.models.ApiResponse;
import com.fooddelivery.kiosk.models.CreateOrderRequest;
import com.fooddelivery.kiosk.models.MenuDto;
import com.fooddelivery.kiosk.models.OrderDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/v1/stores/{storeId}/menus")
    Call<ApiResponse<List<MenuDto>>> getStoreMenus(@Path("storeId") String storeId);

    @POST("api/v1/orders")
    Call<ApiResponse<OrderDto>> createOrder(@Body CreateOrderRequest request);

    @GET("api/v1/orders")
    Call<ApiResponse<List<OrderDto>>> getAllOrders();

    @GET("api/v1/orders/{orderId}")
    Call<ApiResponse<OrderDto>> getOrder(@Path("orderId") String orderId);
}
