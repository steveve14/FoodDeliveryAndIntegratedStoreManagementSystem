package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.MenuDto;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<MenuDto> menus = new ArrayList<>();
    private final OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEdit(MenuDto menu);
        void onDelete(MenuDto menu);
    }

    public ProductAdapter(OnProductActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMenus(List<MenuDto> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        MenuDto menu = menus.get(position);
        holder.tvProductName.setText(menu.getName());
        holder.tvProductPrice.setText(menu.getPrice() + "원");

        if (menu.isAvailable()) {
            holder.tvProductStatus.setText("판매중");
            holder.tvProductStatus.setTextColor(Color.parseColor("#2e7d32"));
        } else {
            holder.tvProductStatus.setText("품절");
            holder.tvProductStatus.setTextColor(Color.parseColor("#F44336"));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(menu);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(menu);
        });
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStatus;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStatus = itemView.findViewById(R.id.tv_product_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
