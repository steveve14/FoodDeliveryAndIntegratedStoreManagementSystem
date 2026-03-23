package com.fooddelivery.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.StoreDto;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<StoreDto> storeList = new ArrayList<>();
    private OnStoreClickListener listener;

    public interface OnStoreClickListener {
        void onStoreClick(StoreDto store);
    }

    public StoreAdapter(OnStoreClickListener listener) {
        this.listener = listener;
    }

    public void setStores(List<StoreDto> stores) {
        this.storeList = stores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        StoreDto store = storeList.get(position);
        holder.tvStoreName.setText(store.getName());
        holder.tvStoreInfo.setText("별점 " + store.getRatingAvg() + " · 최소주문 " + store.getMinOrderAmount() + "원");
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStoreClick(store);
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName, tvStoreInfo;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvStoreInfo = itemView.findViewById(R.id.tv_store_info);
        }
    }
}
