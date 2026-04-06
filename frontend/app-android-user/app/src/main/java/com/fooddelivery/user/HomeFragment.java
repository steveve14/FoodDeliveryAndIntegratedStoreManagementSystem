package com.fooddelivery.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.ApiResponse;
import com.fooddelivery.user.models.StoreDto;
import com.fooddelivery.user.network.ApiClient;
import com.fooddelivery.user.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvStores;
    private StoreAdapter storeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvStores = view.findViewById(R.id.rv_stores);
        rvStores.setLayoutManager(new LinearLayoutManager(getContext()));
        storeAdapter = new StoreAdapter(store -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, StoreDetailFragment.newInstance(store.getId(), store.getName()))
                    .addToBackStack(null)
                    .commit();
        });
        rvStores.setAdapter(storeAdapter);

        loadStores();
    }

    private void loadStores() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getStores(null, null).enqueue(new Callback<ApiResponse<List<StoreDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StoreDto>>> call, Response<ApiResponse<List<StoreDto>>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StoreDto> stores = response.body().getData();
                    storeAdapter.setStores(stores);
                } else {
                    Toast.makeText(requireContext(), "매장 목록 로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StoreDto>>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
