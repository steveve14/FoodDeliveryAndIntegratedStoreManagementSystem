package com.fooddelivery.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartManager.CartItem> cartItems;

    public CartAdapter(List<CartManager.CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartManager.CartItem item = cartItems.get(position);
        holder.tvItemName.setText(item.menu.getName());
        holder.tvItemPrice.setText((item.menu.getPrice() * item.quantity) + "원");
        holder.tvItemQty.setText("수량: " + item.quantity);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvItemQty;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_cart_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvItemQty = itemView.findViewById(R.id.tv_cart_item_qty);
        }
    }
}
