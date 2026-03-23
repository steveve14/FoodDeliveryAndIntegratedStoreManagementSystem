package com.fooddelivery.delivery;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.delivery.models.OrderDto;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<OrderDto> orders = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        OrderDto order = orders.get(position);
        String shortId = order.getId() != null ? order.getId().substring(0, Math.min(8, order.getId().length())) : "?";
        holder.tvHistoryId.setText("주문 #" + shortId);
        holder.tvHistoryDate.setText(order.getCreatedAt() != null ? order.getCreatedAt() : "");
        holder.tvHistoryItems.setText("메뉴 " + (order.getItems() != null ? order.getItems().size() : 0) + "건");
        holder.tvHistoryAmount.setText(order.getTotalAmount() + "원");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryId, tvHistoryDate, tvHistoryItems, tvHistoryAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryId = itemView.findViewById(R.id.tv_history_id);
            tvHistoryDate = itemView.findViewById(R.id.tv_history_date);
            tvHistoryItems = itemView.findViewById(R.id.tv_history_items);
            tvHistoryAmount = itemView.findViewById(R.id.tv_history_amount);
        }
    }
}
