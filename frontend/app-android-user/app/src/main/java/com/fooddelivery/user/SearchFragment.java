package com.fooddelivery.user;

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

import com.fooddelivery.user.models.ApiResponse;
import com.fooddelivery.user.models.OrderDto;
import com.fooddelivery.user.network.ApiClient;
import com.fooddelivery.user.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView tvEmptyOrders;
    private UserOrderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvOrders = view.findViewById(R.id.rv_orders);
        tvEmptyOrders = view.findViewById(R.id.tv_empty_orders);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserOrderAdapter(order -> {
            // Navigate to store detail for reorder
            if (order.getStoreId() != null && getActivity() instanceof MainActivity) {
                Toast.makeText(getContext(), "매장으로 이동합니다.", Toast.LENGTH_SHORT).show();
                // Could navigate to StoreDetailFragment with the storeId
            }
        });
        rvOrders.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMyOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> orders = response.body().getData();
                    if (orders != null && !orders.isEmpty()) {
                        adapter.setOrders(orders);
                        rvOrders.setVisibility(View.VISIBLE);
                        tvEmptyOrders.setVisibility(View.GONE);
                    } else {
                        rvOrders.setVisibility(View.GONE);
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                    }
                } else {
                    rvOrders.setVisibility(View.GONE);
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "주문 내역을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
