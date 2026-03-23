package com.fooddelivery.kiosk;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class KioskCartAdapter extends RecyclerView.Adapter<KioskCartAdapter.CartViewHolder> {

    private List<CartManager.CartItem> cartItemList = new ArrayList<>();
    private final OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public KioskCartAdapter(OnCartChangeListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCartItems(List<CartManager.CartItem> items) {
        this.cartItemList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_kiosk, parent, false);
        return new CartViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartManager.CartItem item = cartItemList.get(position);
        holder.tvItemName.setText(item.menu.getName());
        holder.tvItemPrice.setText((item.menu.getPrice() * item.quantity) + "원");
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        holder.btnPlus.setOnClickListener(v -> {
            CartManager.getInstance().addItem(item.menu);
            if (listener != null) listener.onCartChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            CartManager.getInstance().removeItem(item.menu.getId());
            if (listener != null) listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvQuantity;
        Button btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}
