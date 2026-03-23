package com.fooddelivery.shop;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Phone vs Tablet check
        if (drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 
                R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

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
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
