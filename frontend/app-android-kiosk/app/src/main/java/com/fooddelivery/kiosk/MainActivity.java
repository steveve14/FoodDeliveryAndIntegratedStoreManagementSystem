package com.fooddelivery.kiosk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_menu_order) {
                    selectedFragment = new MenuOrderFragment();
                } else if (id == R.id.nav_order_status) {
                    selectedFragment = new OrderStatusFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            });
        }

        // Initialize fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new MenuOrderFragment())
                    .replace(R.id.cart_fragment_container, new CartFragment())
                    .commit();
            
            if (navigationView != null) {
                navigationView.setCheckedItem(R.id.nav_menu_order);
            }
        }
    }
}
