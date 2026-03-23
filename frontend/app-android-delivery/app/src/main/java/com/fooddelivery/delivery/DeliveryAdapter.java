package com.fooddelivery.delivery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.delivery.models.OrderDto;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    private List<OrderDto> deliveryList = new ArrayList<>();
    private final OnDeliveryActionClickListener listener;

    public interface OnDeliveryActionClickListener {
        void onActionClick(OrderDto order, String nextStatus);
    }

    public DeliveryAdapter(OnDeliveryActionClickListener listener) {
        this.listener = listener;
    }

    public void setDeliveries(List<OrderDto> deliveries) {
        this.deliveryList = deliveries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        OrderDto order = deliveryList.get(position);
        
        String shortId = order.getId() != null ? order.getId().substring(0, Math.min(8, order.getId().length())) : "N/A";
        holder.tvDeliveryId.setText("주문번호: " + shortId);
        holder.tvDeliveryStatus.setText(order.getStatus());
        holder.tvDeliveryTime.setText("주문시간: " + order.getCreatedAt());
        
        String address = order.getAddress() != null && !order.getAddress().isEmpty() ? order.getAddress() : "도착지 정보 없음";
        holder.tvDeliveryAddress.setText("도착지: " + address);

        if ("COOKING".equalsIgnoreCase(order.getStatus())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("픽업 완료 (배달시작)");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onActionClick(order, "DELIVERING");
            });
        } else if ("DELIVERING".equalsIgnoreCase(order.getStatus())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("고객 전달 완료");
            holder.btnAction.setBackgroundColor(0xFF4CAF50); // Green
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onActionClick(order, "DELIVERED");
            });
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeliveryId, tvDeliveryStatus, tvDeliveryAddress, tvDeliveryTime;
        Button btnAction;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeliveryId = itemView.findViewById(R.id.tv_delivery_id);
            tvDeliveryStatus = itemView.findViewById(R.id.tv_delivery_status);
            tvDeliveryAddress = itemView.findViewById(R.id.tv_delivery_address);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
    }
}
