package com.fooddelivery.kiosk;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fooddelivery.kiosk.models.ApiResponse;
import com.fooddelivery.kiosk.models.OrderDto;
import com.fooddelivery.kiosk.network.ApiClient;
import com.fooddelivery.kiosk.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusFragment extends Fragment {

    private TextView tvPreparingOrders;
    private TextView tvServingOrders;
    private final Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvPreparingOrders = view.findViewById(R.id.tv_preparing_orders);
        tvServingOrders = view.findViewById(R.id.tv_serving_orders);
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
                pollingHandler.postDelayed(this, 10000); // Poll every 10 seconds
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
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> allOrders = response.body().getData();
                    
                    StringBuilder preparingStr = new StringBuilder();
                    StringBuilder servingStr = new StringBuilder();

                    for (OrderDto order : allOrders) {
                        // Use first 4 characters of ID as short "order number" for kiosk
                        String shortId = order.getId().substring(0, Math.min(4, order.getId().length())).toUpperCase();

                        if ("PENDING".equalsIgnoreCase(order.getStatus()) || "COOKING".equalsIgnoreCase(order.getStatus())) {
                            preparingStr.append(shortId).append("\n");
                        } else if ("DELIVERING".equalsIgnoreCase(order.getStatus()) || "READY".equalsIgnoreCase(order.getStatus())) {
                            // READY / DELIVERING implies food is ready to pick up
                            servingStr.append(shortId).append("\n");
                        }
                    }

                    tvPreparingOrders.setText(preparingStr.length() > 0 ? preparingStr.toString().trim() : "-");
                    tvServingOrders.setText(servingStr.length() > 0 ? servingStr.toString().trim() : "-");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Throwable t) {
                // Silently ignore network failures during polling
            }
        });
    }
}
