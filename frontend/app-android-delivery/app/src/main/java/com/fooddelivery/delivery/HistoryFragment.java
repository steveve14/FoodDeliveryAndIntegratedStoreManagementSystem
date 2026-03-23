package com.fooddelivery.delivery;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.fooddelivery.delivery.network.ApiClient;
import com.fooddelivery.delivery.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private TextView tvCompletedCount;
    private TextView tvTotalEarnings;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvHistory = view.findViewById(R.id.rv_history);
        tvEmptyHistory = view.findViewById(R.id.tv_empty_history);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvTotalEarnings = view.findViewById(R.id.tv_total_earnings);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter();
        rvHistory.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    @SuppressLint("SetTextI18n")
    private void loadHistory() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> allOrders = response.body().getData();
                    List<OrderDto> completed = new ArrayList<>();
                    int totalEarnings = 0;

                    if (allOrders != null) {
                        for (OrderDto order : allOrders) {
                            String status = order.getStatus();
                            if ("DONE".equalsIgnoreCase(status) || "DELIVERED".equalsIgnoreCase(status)) {
                                completed.add(order);
                                totalEarnings += order.getTotalAmount();
                            }
                        }
                    }

                    tvCompletedCount.setText(completed.size() + " 건");
                    tvTotalEarnings.setText("총 수익: " + totalEarnings + "원");

                    if (!completed.isEmpty()) {
                        adapter.setOrders(completed);
                        rvHistory.setVisibility(View.VISIBLE);
                        tvEmptyHistory.setVisibility(View.GONE);
                    } else {
                        rvHistory.setVisibility(View.GONE);
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "이력 조회 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
