package com.fooddelivery.user;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.OrderDto;

import java.util.ArrayList;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {

    private List<OrderDto> orders = new ArrayList<>();
    private final OnReorderClickListener listener;

    public interface OnReorderClickListener {
        void onReorderClick(OrderDto order);
    }

    public UserOrderAdapter(OnReorderClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_user, parent, false);
        return new OrderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDto order = orders.get(position);

        holder.tvOrderStore.setText("매장 #" + (order.getStoreId() != null ? order.getStoreId().substring(0, Math.min(8, order.getStoreId().length())) : "?"));
        holder.tvOrderId.setText("주문번호 #" + order.getId().substring(0, Math.min(8, order.getId().length())));
        holder.tvOrderItems.setText("메뉴 " + (order.getItems() != null ? order.getItems().size() : 0) + "건");
        holder.tvOrderAmount.setText(order.getTotalAmount() + "원");
        holder.tvOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt() : "");

        // Status badge
        String status = order.getStatus();
        String label;
        int bgColor;
        if ("CREATED".equals(status) || "PENDING".equals(status)) {
            label = "주문 접수";
            bgColor = Color.parseColor("#FF9800");
        } else if ("COOKING".equals(status)) {
            label = "조리 중";
            bgColor = Color.parseColor("#2196F3");
        } else if ("DELIVERING".equals(status)) {
            label = "배달 중";
            bgColor = Color.parseColor("#4CAF50");
        } else if ("DONE".equals(status) || "DELIVERED".equals(status)) {
            label = "배달 완료";
            bgColor = Color.parseColor("#9E9E9E");
        } else if ("CANCELLED".equals(status)) {
            label = "취소됨";
            bgColor = Color.parseColor("#F44336");
        } else {
            label = status != null ? status : "알 수 없음";
            bgColor = Color.parseColor("#757575");
        }
        holder.tvOrderStatus.setText(label);
        holder.tvOrderStatus.setBackgroundColor(bgColor);

        holder.btnReorder.setOnClickListener(v -> {
            if (listener != null) listener.onReorderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderStore, tvOrderId, tvOrderItems, tvOrderAmount, tvOrderDate, tvOrderStatus;
        Button btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStore = itemView.findViewById(R.id.tv_order_store);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
            tvOrderAmount = itemView.findViewById(R.id.tv_order_amount);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            btnReorder = itemView.findViewById(R.id.btn_reorder);
        }
    }
}
