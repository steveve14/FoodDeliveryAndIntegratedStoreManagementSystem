package com.fooddelivery.shop.network;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.CreateMenuRequest;
import com.fooddelivery.shop.models.LoginRequest;
import com.fooddelivery.shop.models.MenuDto;
import com.fooddelivery.shop.models.OrderDto;
import com.fooddelivery.shop.models.OrderStatusRequest;
import com.fooddelivery.shop.models.StoreDto;
import com.fooddelivery.shop.models.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/v1/auth/login")
    Call<ApiResponse<UserDto>> login(@Body LoginRequest request);

    @GET("api/v1/stores")
    Call<ApiResponse<List<StoreDto>>> getStores();

    @GET("api/v1/stores/me")
    Call<ApiResponse<StoreDto>> getMyStore();

    @GET("api/v1/orders/store/{storeId}")
    Call<ApiResponse<List<OrderDto>>> getStoreOrders(@Path("storeId") String storeId);

    @PATCH("api/v1/orders/{id}/status")
    Call<ApiResponse<OrderDto>> updateOrderStatus(@Path("id") String orderId, @Body OrderStatusRequest request);

    // Menu CRUD
    @GET("api/v1/stores/{storeId}/menus")
    Call<ApiResponse<List<MenuDto>>> getMenus(@Path("storeId") String storeId);

    @POST("api/v1/stores/{storeId}/menus")
    Call<ApiResponse<MenuDto>> createMenu(@Path("storeId") String storeId, @Body CreateMenuRequest request);

    @PUT("api/v1/stores/{storeId}/menus/{menuId}")
    Call<ApiResponse<MenuDto>> updateMenu(@Path("storeId") String storeId, @Path("menuId") String menuId, @Body CreateMenuRequest request);

    @DELETE("api/v1/stores/{storeId}/menus/{menuId}")
    Call<ApiResponse<Void>> deleteMenu(@Path("storeId") String storeId, @Path("menuId") String menuId);
}

