package com.fooddelivery.kiosk;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.kiosk.models.MenuDto;

import java.util.ArrayList;
import java.util.List;

public class KioskMenuAdapter extends RecyclerView.Adapter<KioskMenuAdapter.KioskMenuViewHolder> {

    private List<MenuDto> menuList = new ArrayList<>();
    private final OnMenuClickListener listener;

    public interface OnMenuClickListener {
        void onAddCartClick(MenuDto menu);
    }

    public KioskMenuAdapter(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public void setMenus(List<MenuDto> menus) {
        this.menuList = menus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KioskMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_kiosk, parent, false);
        return new KioskMenuViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull KioskMenuViewHolder holder, int position) {
        MenuDto menu = menuList.get(position);
        holder.tvMenuName.setText(menu.getName());
        holder.tvMenuPrice.setText(menu.getPrice() + "원");
        if (menu.isAvailable()) {
            holder.tvMenuStatus.setText("판매중");
            holder.tvMenuStatus.setTextColor(Color.parseColor("#2e7d32"));
        } else {
            holder.tvMenuStatus.setText("판매중지");
            holder.tvMenuStatus.setTextColor(Color.parseColor("#9e9e9e"));
        }
        
        holder.btnAddCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddCartClick(menu);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class KioskMenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuName, tvMenuPrice, tvMenuStatus;
        Button btnAddCart;

        public KioskMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuName = itemView.findViewById(R.id.tv_menu_name);
            tvMenuStatus = itemView.findViewById(R.id.tv_menu_status);
            tvMenuPrice = itemView.findViewById(R.id.tv_menu_price);
            btnAddCart = itemView.findViewById(R.id.btn_add_cart);
        }
    }
}
