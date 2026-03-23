package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.CreateMenuRequest;
import com.fooddelivery.shop.models.MenuDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment {

    private RecyclerView rvProducts;
    private TextView tvEmptyProducts;
    private ProductAdapter adapter;
    private String storeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvProducts = view.findViewById(R.id.rv_products);
        tvEmptyProducts = view.findViewById(R.id.tv_empty_products);
        Button btnAddMenu = view.findViewById(R.id.btn_add_menu);

        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(MenuDto menu) {
                showMenuDialog(menu);
            }

            @Override
            public void onDelete(MenuDto menu) {
                confirmDelete(menu);
            }
        });
        rvProducts.setAdapter(adapter);

        btnAddMenu.setOnClickListener(v -> showMenuDialog(null));

        // Get storeId from StoreManager
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
        loadMenus();
    }

    private void loadMenus() {
        if (storeId == null) {
            tvEmptyProducts.setText("먼저 설정에서 로그인해주세요.");
            tvEmptyProducts.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getMenus(storeId).enqueue(new Callback<ApiResponse<List<MenuDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Response<ApiResponse<List<MenuDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MenuDto> menus = response.body().getData();
                    if (menus != null && !menus.isEmpty()) {
                        adapter.setMenus(menus);
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmptyProducts.setVisibility(View.GONE);
                    } else {
                        rvProducts.setVisibility(View.GONE);
                        tvEmptyProducts.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "메뉴 목록 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showMenuDialog(@Nullable MenuDto existing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(existing != null ? "메뉴 수정" : "새 메뉴 등록");

        View dialogView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        // Use a simple custom layout with EditTexts
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        EditText etName = new EditText(getContext());
        etName.setHint("메뉴 이름");
        if (existing != null) etName.setText(existing.getName());
        layout.addView(etName);

        EditText etPrice = new EditText(getContext());
        etPrice.setHint("가격");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (existing != null) etPrice.setText(String.valueOf(existing.getPrice()));
        layout.addView(etPrice);

        EditText etDesc = new EditText(getContext());
        etDesc.setHint("설명 (선택)");
        if (existing != null && existing.getDescription() != null) etDesc.setText(existing.getDescription());
        layout.addView(etDesc);

        builder.setView(layout);

        builder.setPositiveButton(existing != null ? "수정" : "등록", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "이름과 가격을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            CreateMenuRequest request = new CreateMenuRequest(name, desc, price, true);
            ApiService api = ApiClient.getClient().create(ApiService.class);

            if (existing != null) {
                api.updateMenu(storeId, existing.getId(), request).enqueue(new Callback<ApiResponse<MenuDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Response<ApiResponse<MenuDto>> response) {
                        Toast.makeText(getContext(), "수정 완료", Toast.LENGTH_SHORT).show();
                        loadMenus();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "수정 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                api.createMenu(storeId, request).enqueue(new Callback<ApiResponse<MenuDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Response<ApiResponse<MenuDto>> response) {
                        Toast.makeText(getContext(), "등록 완료", Toast.LENGTH_SHORT).show();
                        loadMenus();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "등록 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void confirmDelete(MenuDto menu) {
        new AlertDialog.Builder(requireContext())
                .setTitle("메뉴 삭제")
                .setMessage(menu.getName() + "을(를) 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    api.deleteMenu(storeId, menu.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                            Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                            loadMenus();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
