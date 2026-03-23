package com.fooddelivery.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.ApiResponse;
import com.fooddelivery.user.models.CreateOrderRequest;
import com.fooddelivery.user.models.OrderDto;
import com.fooddelivery.user.network.ApiClient;
import com.fooddelivery.user.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private TextView tvStoreName, tvTotalPrice;
    private Button btnCheckout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStoreName = view.findViewById(R.id.tv_cart_store_name);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        rvCartItems = view.findViewById(R.id.rv_cart_items);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadCart();

        btnCheckout.setOnClickListener(v -> performCheckout());
    }

    private void loadCart() {
        CartManager cart = CartManager.getInstance();
        if (cart.getItems().isEmpty()) {
            tvStoreName.setText("장바구니가 비어 있습니다.");
            tvTotalPrice.setText("0원");
            btnCheckout.setEnabled(false);
        } else {
            tvStoreName.setText(cart.getStoreName());
            tvTotalPrice.setText(cart.getTotalAmount() + "원");
            btnCheckout.setEnabled(true);
        }

        cartAdapter = new CartAdapter(cart.getItems());
        rvCartItems.setAdapter(cartAdapter);
    }

    private void performCheckout() {
        CartManager cart = CartManager.getInstance();
        if (cart.getItems().isEmpty()) return;

        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        for (CartManager.CartItem item : cart.getItems()) {
            items.add(new CreateOrderRequest.OrderItemRequest(
                    item.menu.getId(),
                    item.quantity,
                    item.menu.getPrice()
            ));
        }

        // Hardcoded address for MVP integration test
        CreateOrderRequest request = new CreateOrderRequest(
                "서울시 강남구 테헤란로 (테스트 주소)",
                cart.getTotalAmount(),
                items
        );

        btnCheckout.setEnabled(false);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createOrder(request).enqueue(new Callback<ApiResponse<OrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDto>> call, Response<ApiResponse<OrderDto>> response) {
                btnCheckout.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "주문이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    cart.clear();
                    loadCart();
                } else {
                    Toast.makeText(getContext(), "주문 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDto>> call, Throwable t) {
                btnCheckout.setEnabled(true);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
