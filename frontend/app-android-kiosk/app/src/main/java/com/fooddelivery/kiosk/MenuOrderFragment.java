package com.fooddelivery.kiosk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.kiosk.models.ApiResponse;
import com.fooddelivery.kiosk.models.MenuDto;
import com.fooddelivery.kiosk.network.ApiClient;
import com.fooddelivery.kiosk.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuOrderFragment extends Fragment {

    private KioskMenuAdapter menuAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvKioskMenus = view.findViewById(R.id.rv_kiosk_menus);
        rvKioskMenus.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        menuAdapter = new KioskMenuAdapter(menu -> {
            CartManager.getInstance().addItem(menu);
            Toast.makeText(getContext(), menu.getName() + "이(가) 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show();
        });
        rvKioskMenus.setAdapter(menuAdapter);

        loadMenus();
    }

    private void loadMenus() {
        String storeId = KioskConfig.getInstance(requireContext()).getStoreId();
        if (storeId == null) {
            Toast.makeText(getContext(), "매장 ID가 설정되지 않았습니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
            return;
        }
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getStoreMenus(storeId).enqueue(new Callback<ApiResponse<List<MenuDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Response<ApiResponse<List<MenuDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MenuDto> menus = response.body().getData();
                    menuAdapter.setMenus(menus);
                } else {
                    Toast.makeText(getContext(), "메뉴 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
