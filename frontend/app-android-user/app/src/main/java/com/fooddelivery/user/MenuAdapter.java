package com.fooddelivery.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.MenuDto;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuDto> menuList = new ArrayList<>();
    private final OnMenuClickListener listener;

    public interface OnMenuClickListener {
        void onMenuClick(MenuDto menu);
    }

    public MenuAdapter(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public void setMenus(List<MenuDto> menus) {
        this.menuList = menus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuDto menu = menuList.get(position);
        holder.tvMenuName.setText(menu.getName());
        holder.tvMenuDesc.setText(menu.getDescription());
        holder.tvMenuPrice.setText(menu.getPrice() + "원");
        if (menu.isAvailable()) {
            holder.tvMenuStatus.setText("판매중");
            holder.tvMenuStatus.setTextColor(Color.parseColor("#2e7d32"));
        } else {
            holder.tvMenuStatus.setText("판매중지");
            holder.tvMenuStatus.setTextColor(Color.parseColor("#9e9e9e"));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClick(menu);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuName, tvMenuDesc, tvMenuPrice, tvMenuStatus;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuName = itemView.findViewById(R.id.tv_menu_name);
            tvMenuDesc = itemView.findViewById(R.id.tv_menu_desc);
            tvMenuPrice = itemView.findViewById(R.id.tv_menu_price);
            tvMenuStatus = itemView.findViewById(R.id.tv_menu_status);
        }
    }
}
