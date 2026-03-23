package com.fooddelivery.kiosk;

import android.annotation.SuppressLint;
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

import com.fooddelivery.kiosk.models.ApiResponse;
import com.fooddelivery.kiosk.models.CreateOrderRequest;
import com.fooddelivery.kiosk.models.OrderDto;
import com.fooddelivery.kiosk.network.ApiClient;
import com.fooddelivery.kiosk.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private TextView tvEmptyCart, tvTotalPrice;
    private Button btnCheckout;
    private KioskCartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvCartItems = view.findViewById(R.id.rv_cart_items);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new KioskCartAdapter(this::updateCartUI);
        rvCartItems.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> performCheckout());

        updateCartUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateCartUI() {
        List<CartManager.CartItem> items = new ArrayList<>(CartManager.getInstance().getCartItems().values());
        if (items.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            btnCheckout.setText("결제 및 주문하기 (0원)");
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
        }
        
        cartAdapter.setCartItems(items);
        
        int total = CartManager.getInstance().getTotalPrice();
        tvTotalPrice.setText(total + "원");
        if (total > 0) {
            btnCheckout.setText("결제 및 주문하기 (" + total + "원)");
        }
    }

    private void performCheckout() {
        List<CartManager.CartItem> cartItems = new ArrayList<>(CartManager.getInstance().getCartItems().values());
        if (cartItems.isEmpty()) return;

        List<CreateOrderRequest.Item> requestItems = new ArrayList<>();
        for (CartManager.CartItem item : cartItems) {
            requestItems.add(new CreateOrderRequest.Item(item.menu.getId(), item.quantity));
        }

        // Hardcoded storeId="1" for MVP kiosk
        CreateOrderRequest request = new CreateOrderRequest("1", requestItems, "CREDIT_CARD", "KIOSK-EAT-IN");
        
        btnCheckout.setEnabled(false);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createOrder(request).enqueue(new Callback<ApiResponse<OrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDto>> call, Response<ApiResponse<OrderDto>> response) {
                btnCheckout.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    OrderDto order = response.body().getData();
                    
                    CartManager.getInstance().clearCart();
                    updateCartUI();

                    Toast.makeText(getContext(), "주문 성공! 주문번호: " + order.getId().substring(0, 8), Toast.LENGTH_LONG).show();
                    
                    // Note: Here we would typically navigate to an OrderStatusFragment passing the order.getId().
                    // Since kiosk architecture simplifies this, showing a Toast is a temporary feedback.
                } else {
                    Toast.makeText(getContext(), "주문 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<OrderDto>> call, @NonNull Throwable t) {
                btnCheckout.setEnabled(true);
                Toast.makeText(getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
