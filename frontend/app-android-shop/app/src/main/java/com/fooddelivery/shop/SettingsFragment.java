package com.fooddelivery.shop;

import android.os.Bundle;
import android.util.Log;
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

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.LoginRequest;
import com.fooddelivery.shop.models.StoreDto;
import com.fooddelivery.shop.models.UserDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private LinearLayout layoutLogin;
    private LinearLayout layoutProfileInfo;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnLogout;
    private TextView tvGreeting, tvStoreName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
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
        tvStoreName = view.findViewById(R.id.tv_store_name);

        updateUIState();

        btnLogin.setOnClickListener(v -> performLogin());
        btnLogout.setOnClickListener(v -> performLogout());
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
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserDto user = response.body().getData();
                    fetchMyStore(user);
                } else {
                    Toast.makeText(getContext(), "로그인 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }

    private void fetchMyStore(UserDto user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getStores().enqueue(new Callback<ApiResponse<List<StoreDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StoreDto>>> call, Response<ApiResponse<List<StoreDto>>> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StoreDto> stores = response.body().getData();
                    boolean storeFound = false;
                    for (StoreDto store : stores) {
                        if (user.getId().equals(store.getOwnerId())) {
                            StoreManager.getInstance().setStore(store.getId(), store.getName(), user.getName());
                            storeFound = true;
                            break;
                        }
                    }
                    if (storeFound) {
                        updateUIState();
                        Toast.makeText(getContext(), "사장님 로그인 성공!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "운영 중인 매장을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                        ApiClient.getCookieJar().clear();
                    }
                } else {
                    Toast.makeText(getContext(), "매장 정보 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StoreDto>>> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        ApiClient.getCookieJar().clear();
        StoreManager.getInstance().clear();
        etEmail.setText("");
        etPassword.setText("");
        updateUIState();
    }

    private void updateUIState() {
        if (StoreManager.getInstance().getStoreId() != null) {
            layoutLogin.setVisibility(View.GONE);
            layoutProfileInfo.setVisibility(View.VISIBLE);
            tvGreeting.setText(StoreManager.getInstance().getOwnerName() + " 사장님, 환영합니다!");
            tvStoreName.setText("운영 매장: " + StoreManager.getInstance().getStoreName());
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutProfileInfo.setVisibility(View.GONE);
        }
    }
}
