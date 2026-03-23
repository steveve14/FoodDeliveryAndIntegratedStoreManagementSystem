package com.fooddelivery.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fooddelivery.user.models.ApiResponse;
import com.fooddelivery.user.models.LoginRequest;
import com.fooddelivery.user.models.UserDto;
import com.fooddelivery.user.network.ApiClient;
import com.fooddelivery.user.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private View loginLayout;
    private View profileInfoLayout;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvUserName, tvUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginLayout = view.findViewById(R.id.login_layout);
        profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);
        
        btnLogin.setEnabled(false);

        apiService.login(request).enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserDto user = response.body().getData();
                    showProfile(user);
                } else {
                    Toast.makeText(getContext(), "로그인 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfile(UserDto user) {
        loginLayout.setVisibility(View.GONE);
        profileInfoLayout.setVisibility(View.VISIBLE);
        
        tvUserName.setText(user.getName() + " 님");
        tvUserEmail.setText(user.getEmail());
    }
}
