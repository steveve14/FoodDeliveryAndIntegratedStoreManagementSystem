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
import com.fooddelivery.user.models.MenuDto;
import com.fooddelivery.user.network.ApiClient;
import com.fooddelivery.user.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDetailFragment extends Fragment {

    private String storeId;
    private String storeName;
    private RecyclerView rvMenus;
    private MenuAdapter menuAdapter;
    private TextView tvStoreTitle;

    public static StoreDetailFragment newInstance(String storeId, String storeName) {
        StoreDetailFragment fragment = new StoreDetailFragment();
        Bundle args = new Bundle();
        args.putString("STORE_ID", storeId);
        args.putString("STORE_NAME", storeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storeId = getArguments().getString("STORE_ID");
            storeName = getArguments().getString("STORE_NAME");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStoreTitle = view.findViewById(R.id.tv_store_title);
        tvStoreTitle.setText(storeName);

        rvMenus = view.findViewById(R.id.rv_menus);
        rvMenus.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new MenuAdapter(menu -> {
            CartManager.getInstance().addItem(menu, storeId, storeName);
            Toast.makeText(getContext(), menu.getName() + " 장바구니에 담음", Toast.LENGTH_SHORT).show();
        });
        rvMenus.setAdapter(menuAdapter);

        loadMenus();
    }

    private void loadMenus() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMenus(storeId).enqueue(new Callback<ApiResponse<List<MenuDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuDto>>> call, Response<ApiResponse<List<MenuDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    menuAdapter.setMenus(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "메뉴 로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MenuDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
