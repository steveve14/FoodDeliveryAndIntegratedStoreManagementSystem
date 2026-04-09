package com.fooddelivery.user;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvStores;
    private StoreAdapter storeAdapter;

    // Countdown timer
    private TextView tvCountdown;
    private Handler countdownHandler;
    private Runnable countdownRunnable;
    private long remainingMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        rvStores = view.findViewById(R.id.rv_stores);
        rvStores.setLayoutManager(new LinearLayoutManager(getContext()));
        storeAdapter = new StoreAdapter(store ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, StoreDetailFragment.newInstance(store.getId(), store.getName()))
                        .addToBackStack(null)
                        .commit());
        rvStores.setAdapter(storeAdapter);

        // Search bar → navigate to SearchFragment
        EditText etSearch = view.findViewById(R.id.et_search);
        if (etSearch != null) {
            etSearch.setOnClickListener(v -> navigateTo(new SearchFragment()));
        }
        view.findViewById(R.id.btn_search_top).setOnClickListener(v -> navigateTo(new SearchFragment()));

        // Category clicks → SearchFragment (pass category via Bundle)
        int[] catIds = {
                R.id.cat_korean, R.id.cat_burger, R.id.cat_pizza,
                R.id.cat_chinese, R.id.cat_japanese,
                R.id.cat_snack, R.id.cat_chicken, R.id.cat_cafe
        };
        String[] catNames = {"한식", "버거", "피자", "중식", "일식", "분식", "치킨", "카페"};
        for (int i = 0; i < catIds.length; i++) {
            final String category = catNames[i];
            View catView = view.findViewById(catIds[i]);
            if (catView != null) {
                catView.setOnClickListener(v -> navigateToCategory(category));
            }
        }

        // Countdown timer (~8 hours)
        tvCountdown = view.findViewById(R.id.tv_countdown);
        remainingMillis = TimeUnit.HOURS.toMillis(8)
                + TimeUnit.MINUTES.toMillis(12)
                + TimeUnit.SECONDS.toMillis(45);
        startCountdown();

        loadStores();
    }

    private void navigateTo(Fragment target) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, target)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToCategory(String category) {
        Bundle args = new Bundle();
        args.putString("category", category);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(args);
        navigateTo(searchFragment);
    }

    private void startCountdown() {
        countdownHandler = new Handler(Looper.getMainLooper());
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || tvCountdown == null) return;
                if (remainingMillis <= 0) {
                    tvCountdown.setText("00:00:00");
                    return;
                }
                long hours   = TimeUnit.MILLISECONDS.toHours(remainingMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60;
                tvCountdown.setText(String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", hours, minutes, seconds));
                remainingMillis -= 1000;
                countdownHandler.postDelayed(this, 1000);
            }
        };
        countdownHandler.post(countdownRunnable);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countdownHandler != null && countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
    }
}

