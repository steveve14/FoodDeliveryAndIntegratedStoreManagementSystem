package com.fooddelivery.delivery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fooddelivery.delivery.models.ApiResponse;
import com.fooddelivery.delivery.models.LoginRequest;
import com.fooddelivery.delivery.models.OrderDto;
import com.fooddelivery.delivery.models.UserDto;
import com.fooddelivery.delivery.network.ApiClient;
import com.fooddelivery.delivery.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutLogin;
    private LinearLayout layoutProfileInfo;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnLogout;
    private TextView tvGreeting, tvEmail;
    private TextView tvStatCompleted, tvStatEarnings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        layoutLogin = view.findViewById(R.id.layout_login);
        layoutProfileInfo = view.findViewById(R.id.layout_profile_info);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvEmail = view.findViewById(R.id.tv_email);
        tvStatCompleted = view.findViewById(R.id.tv_stat_completed);
        tvStatEarnings = view.findViewById(R.id.tv_stat_earnings);

        updateUIState();

        btnLogin.setOnClickListener(v -> performLogin());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DeliveryManager.getInstance().getRiderId() != null) {
            loadDeliveryStats();
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserDto>> call, @NonNull Response<ApiResponse<UserDto>> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserDto user = response.body().getData();
                    DeliveryManager.getInstance().setRider(user.getId(), user.getName(), user.getEmail());
                    Toast.makeText(getContext(), "라이더 로그인 성공!", Toast.LENGTH_SHORT).show();
                    updateUIState();
                    loadDeliveryStats();
                } else {
                    Toast.makeText(getContext(), "로그인 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserDto>> call, @NonNull Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        ApiClient.getCookieJar().clear();
        DeliveryManager.getInstance().clear();
        etEmail.setText("");
        etPassword.setText("");
        updateUIState();
    }

    @SuppressLint("SetTextI18n")
    private void updateUIState() {
        if (DeliveryManager.getInstance().getRiderId() != null) {
            layoutLogin.setVisibility(View.GONE);
            layoutProfileInfo.setVisibility(View.VISIBLE);
            tvGreeting.setText(DeliveryManager.getInstance().getRiderName() + " 라이더님, 환영합니다!");
            tvEmail.setText(DeliveryManager.getInstance().getRiderEmail());
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutProfileInfo.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadDeliveryStats() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderDto> allOrders = response.body().getData();
                    int completedCount = 0;
                    int totalEarnings = 0;
                    if (allOrders != null) {
                        for (OrderDto order : allOrders) {
                            String status = order.getStatus();
                            if ("DONE".equalsIgnoreCase(status) || "DELIVERED".equalsIgnoreCase(status)) {
                                completedCount++;
                                totalEarnings += order.getTotalAmount();
                            }
                        }
                    }
                    tvStatCompleted.setText(String.valueOf(completedCount));
                    tvStatEarnings.setText(totalEarnings + "원");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Throwable t) {
                // Silently fail for stats
            }
        });
    }
}

