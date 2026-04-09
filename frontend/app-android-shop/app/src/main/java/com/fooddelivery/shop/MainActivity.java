package com.fooddelivery.shop;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.StoreDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView tvNavStoreName;
    private TextView tvNavStoreStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Nav header 바인딩
        if (navigationView != null) {
            View navHeader = navigationView.getHeaderView(0);
            tvNavStoreName = navHeader.findViewById(R.id.tv_store_name);
            tvNavStoreStatus = navHeader.findViewById(R.id.tv_store_status);
        }

        // Phone vs Tablet check
        if (drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                Fragment selectedFragment = null;

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    toolbar.setTitle("사장님 대시보드");
                } else if (id == R.id.nav_menu_management) {
                    selectedFragment = new ProductsFragment();
                    toolbar.setTitle("메뉴/상품 관리");
                } else if (id == R.id.nav_order_reception) {
                    selectedFragment = new OrdersFragment();
                    toolbar.setTitle("실시간 주문접수");
                } else if (id == R.id.nav_delivery) {
                    selectedFragment = new DeliveryFragment();
                    toolbar.setTitle("배달 관리");
                } else if (id == R.id.nav_review_management) {
                    selectedFragment = new ReviewsFragment();
                    toolbar.setTitle("리뷰 관리");
                } else if (id == R.id.nav_sales_statistics) {
                    selectedFragment = new SalesFragment();
                    toolbar.setTitle("매출·정산");
                } else if (id == R.id.nav_store_management) {
                    selectedFragment = new StoreManagementFragment();
                    toolbar.setTitle("가게 관리");
                } else if (id == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    toolbar.setTitle("매장 설정");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            });

            // Default Fragment on start
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatusDisplay();
    }

    /** 영업상태를 툴바 subtitle과 드로어 헤더에 반영합니다. */
    public void refreshStatusDisplay() {
        StoreManager sm = StoreManager.getInstance();
        String status = sm.getStatus();
        if (status != null) {
            applyStatusToViews(status, sm.getStoreName());
        } else if (sm.getStoreId() != null) {
            fetchAndDisplayStoreStatus();
        }
    }

    private void fetchAndDisplayStoreStatus() {
        String storeId = StoreManager.getInstance().getStoreId();
        if (storeId == null) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getStore(storeId).enqueue(new Callback<ApiResponse<StoreDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<StoreDto>> call,
                                   @NonNull Response<ApiResponse<StoreDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StoreDto store = response.body().getData();
                    if (store != null && store.getStatus() != null) {
                        StoreManager.getInstance().setStatus(store.getStatus());
                        runOnUiThread(() -> applyStatusToViews(store.getStatus(), store.getName()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<StoreDto>> call, @NonNull Throwable t) {
                // 상태 로드 실패 시 무시 (표시 유지)
            }
        });
    }

    private void applyStatusToViews(String status, String storeName) {
        boolean isOpen = "OPEN".equals(status);
        String label = isOpen ? "● 영업중" : "● 영업종료";
        int statusColor = isOpen ? getColor(R.color.secondary) : getColor(R.color.error);

        // 툴바 subtitle에 색상 적용
        SpannableString subtitle = new SpannableString(label);
        subtitle.setSpan(new ForegroundColorSpan(statusColor), 0, label.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setSubtitle(subtitle);

        // 드로어 헤더 업데이트
        if (tvNavStoreName != null && storeName != null) {
            tvNavStoreName.setText(storeName);
        }
        if (tvNavStoreStatus != null) {
            tvNavStoreStatus.setText(label);
            int navStatusColor = isOpen
                    ? getColor(R.color.primary_fixed_dim)
                    : getColor(R.color.error_container);
            tvNavStoreStatus.setTextColor(navStatusColor);
        }
    }

}
