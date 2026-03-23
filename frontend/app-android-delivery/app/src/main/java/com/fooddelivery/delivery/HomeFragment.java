package com.fooddelivery.delivery;

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

import com.fooddelivery.delivery.models.ApiResponse;
import com.fooddelivery.delivery.models.OrderDto;
import com.fooddelivery.delivery.models.OrderStatusRequest;
import com.fooddelivery.delivery.network.ApiClient;
import com.fooddelivery.delivery.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvDeliveries;
    private TextView tvEmptyDeliveries;
    private DeliveryAdapter deliveryAdapter;
    private Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDeliveries = view.findViewById(R.id.rv_deliveries);
        tvEmptyDeliveries = view.findViewById(R.id.tv_empty_deliveries);

        rvDeliveries.setLayoutManager(new LinearLayoutManager(getContext()));
        deliveryAdapter = new DeliveryAdapter((order, nextStatus) -> updateDeliveryStatus(order.getId(), nextStatus));
        rvDeliveries.setAdapter(deliveryAdapter);
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
                loadDeliveries();
                pollingHandler.postDelayed(this, 10000); // 10s
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void stopPolling() {
        if (pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void loadDeliveries() {
        if (DeliveryManager.getInstance().getRiderId() == null) {
            tvEmptyDeliveries.setText("배달원 로그인을 먼저 해주세요.");
            tvEmptyDeliveries.setVisibility(View.VISIBLE);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderDto>>> call, Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> allOrders = response.body().getData();
                    List<OrderDto> activeDeliveries = new ArrayList<>();
                    
                    for (OrderDto order : allOrders) {
                        if ("COOKING".equalsIgnoreCase(order.getStatus()) || "DELIVERING".equalsIgnoreCase(order.getStatus())) {
                            activeDeliveries.add(order);
                        }
                    }

                    if (activeDeliveries.isEmpty()) {
                        tvEmptyDeliveries.setText("현재 대기 중인 배달 로또가 없습니다.");
                        tvEmptyDeliveries.setVisibility(View.VISIBLE);
                        rvDeliveries.setVisibility(View.GONE);
                    } else {
                        tvEmptyDeliveries.setVisibility(View.GONE);
                        rvDeliveries.setVisibility(View.VISIBLE);
                        deliveryAdapter.setDeliveries(activeDeliveries);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrderDto>>> call, Throwable t) {
                // Ignore silent failures for polling
            }
        });
    }

    private void updateDeliveryStatus(String orderId, String newStatus) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateOrderStatus(orderId, new OrderStatusRequest(newStatus)).enqueue(new Callback<ApiResponse<OrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDto>> call, Response<ApiResponse<OrderDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "배달 상태가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    loadDeliveries(); // reload
                } else {
                    Toast.makeText(getContext(), "상태 변경 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDto>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
