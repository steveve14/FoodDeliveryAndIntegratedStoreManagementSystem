package com.fooddelivery.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.user.models.StoreDto;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FEATURED = 0;
    private static final int VIEW_TYPE_SIDE     = 1;

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

    @Override
    public int getItemViewType(int position) {
        // Pattern: FEATURED, SIDE, FEATURED, SIDE …
        return (position % 2 == 0) ? VIEW_TYPE_FEATURED : VIEW_TYPE_SIDE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_FEATURED) {
            View view = inflater.inflate(R.layout.item_store_featured, parent, false);
            return new FeaturedViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_store_side, parent, false);
            return new SideViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoreDto store = storeList.get(position);
        if (holder instanceof FeaturedViewHolder) {
            bindFeatured((FeaturedViewHolder) holder, store);
        } else {
            bindSide((SideViewHolder) holder, store);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStoreClick(store);
        });
    }

    private void bindFeatured(FeaturedViewHolder h, StoreDto store) {
        h.tvStoreName.setText(store.getName());
        h.tvRating.setText("★ " + String.format("%.1f", store.getRatingAvg()));
        if (store.getReviewCount() > 0) {
            h.tvReviewCount.setText("(" + store.getReviewCount() + "+)");
        }
        if (store.getEta() != null && !store.getEta().isEmpty()) {
            h.tvEta.setText(store.getEta());
        }
        // Minimum order as price
        if (store.getMinOrderAmount() > 0) {
            h.tvPrice.setText("최소 " + store.getMinOrderAmount() + "원");
        }
        // Promo from API field
        if (store.getPromo() != null && !store.getPromo().isEmpty()) {
            h.tvDiscount.setText(store.getPromo());
            h.tvDiscount.setVisibility(View.VISIBLE);
        }
        // Category-based placeholder color on ImageView
        setCategoryColor(h.ivStoreImage, store.getCategory());
    }

    private void bindSide(SideViewHolder h, StoreDto store) {
        h.tvStoreName.setText(store.getName());
        if (store.getDescription() != null) {
            h.tvDescription.setText(store.getDescription());
        }
        h.tvMinOrder.setText("최소주문 " + store.getMinOrderAmount() + "원");
        // Show free delivery badge if deliveryFee is "0" or null
        String fee = store.getDeliveryFee();
        if (fee == null || fee.equals("0") || fee.equals("0원") || fee.isEmpty()) {
            h.tvFreeDelivery.setVisibility(View.VISIBLE);
        }
        // Promo → hot badge
        if (store.getPromo() != null && !store.getPromo().isEmpty()) {
            h.tvHotBadge.setVisibility(View.VISIBLE);
        }
        setCategoryColor(h.ivStoreImage, store.getCategory());
    }

    /** Sets a category-representative tint color as background placeholder. */
    private void setCategoryColor(ImageView iv, String category) {
        if (category == null) return;
        int color;
        switch (category) {
            case "한식": color = 0xFFFDE68A; break; // amber-100
            case "중식": color = 0xFFBFDBFE; break; // blue-100
            case "일식": color = 0xFFD1FAE5; break; // green-100
            case "치킨": color = 0xFFFEE2E2; break; // red-100
            case "피자": color = 0xFFFFEDD5; break; // orange-100
            case "카페": color = 0xFFE9D5FF; break; // purple-100
            default:    color = 0xFFE5E7EB; break; // gray-100
        }
        iv.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    // ── ViewHolders ──

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStoreImage;
        TextView tvStoreName, tvRating, tvReviewCount, tvEta,
                 tvDiscount, tvTimeSale, tvOriginalPrice, tvPrice;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoreImage     = itemView.findViewById(R.id.iv_store_image);
            tvStoreName      = itemView.findViewById(R.id.tv_store_name);
            tvRating         = itemView.findViewById(R.id.tv_rating);
            tvReviewCount    = itemView.findViewById(R.id.tv_review_count);
            tvEta            = itemView.findViewById(R.id.tv_eta);
            tvDiscount       = itemView.findViewById(R.id.tv_discount);
            tvTimeSale       = itemView.findViewById(R.id.tv_time_sale);
            tvOriginalPrice  = itemView.findViewById(R.id.tv_original_price);
            tvPrice          = itemView.findViewById(R.id.tv_price);
        }
    }

    static class SideViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStoreImage;
        TextView tvStoreName, tvDescription, tvFreeDelivery, tvMinOrder, tvHotBadge;

        SideViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoreImage   = itemView.findViewById(R.id.iv_store_image);
            tvStoreName    = itemView.findViewById(R.id.tv_store_name);
            tvDescription  = itemView.findViewById(R.id.tv_description);
            tvFreeDelivery = itemView.findViewById(R.id.tv_free_delivery);
            tvMinOrder     = itemView.findViewById(R.id.tv_min_order);
            tvHotBadge     = itemView.findViewById(R.id.tv_hot_badge);
        }
    }
}
