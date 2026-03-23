package com.fooddelivery.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.OrderDto;
import com.fooddelivery.shop.models.OrderItemDto;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderDto> orderList = new ArrayList<>();
    private final OnOrderActionClickListener listener;

    public interface OnOrderActionClickListener {
        void onActionClick(OrderDto order, String actionStatus);
    }

    public OrderAdapter(OnOrderActionClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDto order = orderList.get(position);
        
        String shortId = order.getId() != null ? order.getId().substring(0, Math.min(8, order.getId().length())) : "N/A";
        holder.tvOrderId.setText("주문번호: " + shortId);
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTime.setText("주문시간: " + order.getCreatedAt());
        holder.tvOrderTotal.setText("결제금액: " + order.getTotalAmount() + "원");

        StringBuilder itemsStr = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItemDto item : order.getItems()) {
                itemsStr.append("제품ID ").append(item.getProductId().substring(0, Math.min(4, item.getProductId().length())))
                        .append(" x").append(item.getQuantity()).append("\n");
            }
        }
        holder.tvOrderItems.setText(itemsStr.toString().trim());

        String status = order.getStatus() != null ? order.getStatus().toUpperCase() : "";
        
        // Show/hide buttons based on status
        if ("PENDING".equals(status) || "CREATED".equals(status)) {
            holder.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnShip.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else if ("COOKING".equals(status)) {
            holder.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnShip.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActionButtons.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onActionClick(order, "COOKING");
        });

        holder.btnShip.setOnClickListener(v -> {
            if (listener != null) listener.onActionClick(order, "DELIVERING");
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onActionClick(order, "CANCELLED");
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderTime, tvOrderItems, tvOrderTotal;
        LinearLayout layoutActionButtons;
        Button btnAccept, btnShip, btnCancel;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            layoutActionButtons = itemView.findViewById(R.id.layout_action_buttons);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnShip = itemView.findViewById(R.id.btn_ship);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
