package com.fooddelivery.shop;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.OrderDto;
import com.fooddelivery.shop.models.OrderStatusRequest;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView tvEmptyOrders;
    private OrderAdapter orderAdapter;
    private Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrders = view.findViewById(R.id.rv_orders);
        tvEmptyOrders = view.findViewById(R.id.tv_empty_orders);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter((order, actionStatus) -> updateOrderStatus(order.getId(), actionStatus));
        rvOrders.setAdapter(orderAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPolling();
    }

    private void startPolling() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadOrders();
                pollingHandler.postDelayed(this, 10000); // 10s polling
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void stopPolling() {
        if (pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void loadOrders() {
        if (StoreManager.getInstance().getStoreId() == null) {
            tvEmptyOrders.setText("사장님 로그인을 먼저 해주세요.");
            tvEmptyOrders.setVisibility(View.VISIBLE);
            return;
        }

        String storeId = StoreManager.getInstance().getStoreId();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getStoreOrders(storeId).enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderDto>>> call, Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> orders = response.body().getData();
                    if (orders.isEmpty()) {
                        tvEmptyOrders.setText("들어온 주문이 없습니다.");
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        tvEmptyOrders.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        orderAdapter.setOrders(orders);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrderDto>>> call, Throwable t) {
                // Ignore silent failures for polling
            }
        });
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateOrderStatus(orderId, new OrderStatusRequest(newStatus)).enqueue(new Callback<ApiResponse<OrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDto>> call, Response<ApiResponse<OrderDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "주문 상태가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    loadOrders(); // reload
                } else {
                    Toast.makeText(getContext(), "주문 상태 변경 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDto>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
