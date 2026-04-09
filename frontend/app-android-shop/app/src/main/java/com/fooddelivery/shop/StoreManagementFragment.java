package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.StoreDto;
import com.fooddelivery.shop.models.UpdateStoreRequest;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreManagementFragment extends Fragment {

    private TextInputEditText etStoreName, etStoreDescription, etCategory, etOpeningHours, etMinOrderAmount;
    private TextView tvStoreRating, tvStoreReviewCount, tvStoreEmpty;
    private MaterialButtonToggleGroup toggleStatus;
    private Button btnSaveStore;
    private String storeId;
    private String currentStatus = "OPEN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etStoreName = view.findViewById(R.id.et_store_name);
        etStoreDescription = view.findViewById(R.id.et_store_description);
        etCategory = view.findViewById(R.id.et_store_category);
        etOpeningHours = view.findViewById(R.id.et_opening_hours);
        etMinOrderAmount = view.findViewById(R.id.et_min_order_amount);
        tvStoreRating = view.findViewById(R.id.tv_store_rating);
        tvStoreReviewCount = view.findViewById(R.id.tv_store_review_count);
        tvStoreEmpty = view.findViewById(R.id.tv_store_empty);
        toggleStatus = view.findViewById(R.id.toggle_status);
        btnSaveStore = view.findViewById(R.id.btn_save_store);

        toggleStatus.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btn_status_open) {
                currentStatus = "OPEN";
            } else if (checkedId == R.id.btn_status_closed) {
                currentStatus = "CLOSED";
            }
        });

        btnSaveStore.setOnClickListener(v -> saveStore());

        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
        loadStoreInfo();
    }

    private void loadStoreInfo() {
        if (storeId == null) {
            tvStoreEmpty.setVisibility(View.VISIBLE);
            return;
        }

        tvStoreEmpty.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getStore(storeId).enqueue(new Callback<ApiResponse<StoreDto>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<StoreDto>> call,
                                   @NonNull Response<ApiResponse<StoreDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StoreDto store = response.body().getData();
                    if (store == null) return;

                    etStoreName.setText(store.getName() != null ? store.getName() : "");
                    etStoreDescription.setText(store.getDescription() != null ? store.getDescription() : "");
                    etCategory.setText(store.getCategory() != null ? store.getCategory() : "");
                    etOpeningHours.setText(store.getOpeningHours() != null ? store.getOpeningHours() : "");
                    etMinOrderAmount.setText(String.valueOf(store.getMinOrderAmount()));

                    if (store.getRatingAvg() > 0) {
                        tvStoreRating.setText(String.format("%.1f", store.getRatingAvg()));
                    } else {
                        tvStoreRating.setText("0.0");
                    }
                    tvStoreReviewCount.setText(store.getReviewCount() + "건");

                    String status = store.getStatus();
                    if (status != null) {
                        setStatus(status);
                        StoreManager.getInstance().setStatus(status);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refreshStatusDisplay();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<StoreDto>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "가게 정보 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatus(String status) {
        currentStatus = status;
        if ("OPEN".equals(status)) {
            toggleStatus.check(R.id.btn_status_open);
        } else {
            toggleStatus.check(R.id.btn_status_closed);
        }
    }

    private void saveStore() {
        if (storeId == null) {
            Toast.makeText(getContext(), "먼저 로그인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = getText(etStoreName);
        String description = getText(etStoreDescription);
        String category = getText(etCategory);
        String openingHours = getText(etOpeningHours);
        String minOrderStr = getText(etMinOrderAmount);

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "가게 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int minOrder = 0;
        if (!minOrderStr.isEmpty()) {
            try {
                minOrder = Integer.parseInt(minOrderStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "최소 주문 금액을 확인하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UpdateStoreRequest request = new UpdateStoreRequest(
                name, description, currentStatus, openingHours, category, minOrder);

        btnSaveStore.setEnabled(false);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.updateStore(storeId, request).enqueue(new Callback<ApiResponse<StoreDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<StoreDto>> call,
                                   @NonNull Response<ApiResponse<StoreDto>> response) {
                btnSaveStore.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "가게 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // Update StoreManager name
                    StoreDto updated = response.body().getData();
                    if (updated != null) {
                        StoreManager.getInstance().setStore(
                                updated.getId(), updated.getName(),
                                StoreManager.getInstance().getOwnerName());
                        StoreManager.getInstance().setStatus(currentStatus);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refreshStatusDisplay();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "저장 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<StoreDto>> call, @NonNull Throwable t) {
                btnSaveStore.setEnabled(true);
                Toast.makeText(getContext(), "저장 실패: 서버 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
