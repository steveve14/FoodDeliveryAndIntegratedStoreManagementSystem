package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.OrderDto;
import com.fooddelivery.shop.models.OrderItemDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesFragment extends Fragment {

    private TextView tvTotalRevenue, tvCompletedCount, tvOrderCountSales, tvAvgOrderSales, tvCancelCount;
    private TextView tvStatusEmpty, tvRecentEmpty;
    private LinearLayout layoutStatusBreakdown, layoutRecentOrders;
    private String storeId;
    private String currentPeriod = "today";

    private List<OrderDto> allOrders = new ArrayList<>();

    private static final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvOrderCountSales = view.findViewById(R.id.tv_order_count_sales);
        tvAvgOrderSales = view.findViewById(R.id.tv_avg_order_sales);
        tvCancelCount = view.findViewById(R.id.tv_cancel_count);
        tvStatusEmpty = view.findViewById(R.id.tv_status_empty);
        tvRecentEmpty = view.findViewById(R.id.tv_recent_empty);
        layoutStatusBreakdown = view.findViewById(R.id.layout_status_breakdown);
        layoutRecentOrders = view.findViewById(R.id.layout_recent_orders);

        // Period chips
        Chip chipToday = view.findViewById(R.id.chip_today);
        Chip chipWeek = view.findViewById(R.id.chip_week);
        Chip chipMonth = view.findViewById(R.id.chip_month);
        Chip chipAll = view.findViewById(R.id.chip_all_period);

        View.OnClickListener chipListener = v -> {
            chipToday.setChecked(v == chipToday);
            chipWeek.setChecked(v == chipWeek);
            chipMonth.setChecked(v == chipMonth);
            chipAll.setChecked(v == chipAll);

            if (v == chipToday) currentPeriod = "today";
            else if (v == chipWeek) currentPeriod = "week";
            else if (v == chipMonth) currentPeriod = "month";
            else if (v == chipAll) currentPeriod = "all";

            calculateStats();
        };

        chipToday.setOnClickListener(chipListener);
        chipWeek.setOnClickListener(chipListener);
        chipMonth.setOnClickListener(chipListener);
        chipAll.setOnClickListener(chipListener);

        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
        loadOrders();
    }

    private void loadOrders() {
        if (storeId == null) {
            tvTotalRevenue.setText("₩0");
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getStoreOrders(storeId).enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderDto>>> call,
                                   @NonNull Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allOrders = response.body().getData();
                    if (allOrders == null) allOrders = new ArrayList<>();
                    calculateStats();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "주문 데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateStats() {
        List<OrderDto> filtered = filterByPeriod(allOrders);

        long totalRevenue = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        Map<String, Integer> statusCounts = new HashMap<>();

        for (OrderDto order : filtered) {
            String status = order.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);

            if ("COMPLETED".equals(status) || "DELIVERED".equals(status)
                    || "DELIVERING".equals(status) || "READY".equals(status)
                    || "PREPARING".equals(status) || "CONFIRMED".equals(status)) {
                totalRevenue += order.getTotalAmount();
                completedCount++;
            }
            if ("CANCELLED".equals(status)) {
                cancelledCount++;
            }
        }

        tvTotalRevenue.setText("₩" + currencyFormat.format(totalRevenue));
        tvCompletedCount.setText("완료 주문 " + completedCount + "건");
        tvOrderCountSales.setText(filtered.size() + "건");
        tvCancelCount.setText(cancelledCount + "건");

        if (completedCount > 0) {
            long avg = totalRevenue / completedCount;
            tvAvgOrderSales.setText("₩" + currencyFormat.format(avg));
        } else {
            tvAvgOrderSales.setText("₩0");
        }

        // Status breakdown
        buildStatusBreakdown(statusCounts);

        // Recent completed orders
        buildRecentOrders(filtered);
    }

    @SuppressLint("SetTextI18n")
    private void buildStatusBreakdown(Map<String, Integer> statusCounts) {
        layoutStatusBreakdown.removeAllViews();

        if (statusCounts.isEmpty()) {
            layoutStatusBreakdown.addView(tvStatusEmpty);
            return;
        }

        String[] statusOrder = {"CREATED", "CONFIRMED", "PREPARING", "READY", "DELIVERING", "COMPLETED", "CANCELLED"};
        String[] statusLabels = {"신규", "확인됨", "조리중", "준비완료", "배달중", "완료", "취소"};

        for (int i = 0; i < statusOrder.length; i++) {
            Integer count = statusCounts.get(statusOrder[i]);
            if (count != null && count > 0) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);

                TextView tvLabel = new TextView(getContext());
                tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                tvLabel.setText(statusLabels[i]);
                tvLabel.setTextAppearance(getContext(), R.style.TextAppearance_App_Body_Medium);

                TextView tvCount = new TextView(getContext());
                tvCount.setText(count + "건");
                tvCount.setTextAppearance(getContext(), R.style.TextAppearance_App_Title_Small);

                row.addView(tvLabel);
                row.addView(tvCount);
                layoutStatusBreakdown.addView(row);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void buildRecentOrders(List<OrderDto> filtered) {
        layoutRecentOrders.removeAllViews();

        List<OrderDto> completed = new ArrayList<>();
        for (OrderDto order : filtered) {
            if ("COMPLETED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) {
                completed.add(order);
            }
        }

        if (completed.isEmpty()) {
            layoutRecentOrders.addView(tvRecentEmpty);
            return;
        }

        int limit = Math.min(completed.size(), 10);
        for (int i = 0; i < limit; i++) {
            OrderDto order = completed.get(i);

            com.google.android.material.card.MaterialCardView card =
                    new com.google.android.material.card.MaterialCardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = 12;
            card.setLayoutParams(cardParams);
            card.setCardElevation(2);
            card.setRadius(16);
            card.setContentPadding(20, 16, 20, 16);

            LinearLayout content = new LinearLayout(getContext());
            content.setOrientation(LinearLayout.VERTICAL);

            // Order ID + Amount
            LinearLayout header = new LinearLayout(getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvId = new TextView(getContext());
            tvId.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            String orderId = order.getId();
            if (orderId != null && orderId.length() > 8) {
                orderId = "#" + orderId.substring(0, 8);
            }
            tvId.setText(orderId);
            tvId.setTextAppearance(getContext(), R.style.TextAppearance_App_Title_Small);

            TextView tvAmount = new TextView(getContext());
            tvAmount.setText("₩" + currencyFormat.format(order.getTotalAmount()));
            tvAmount.setTextAppearance(getContext(), R.style.TextAppearance_App_Title_Small);

            header.addView(tvId);
            header.addView(tvAmount);

            // Items summary
            TextView tvItems = new TextView(getContext());
            StringBuilder itemText = new StringBuilder();
            if (order.getItems() != null) {
                for (OrderItemDto item : order.getItems()) {
                    if (itemText.length() > 0) itemText.append(", ");
                    itemText.append(item.getProductId()).append(" x").append(item.getQuantity());
                }
            }
            tvItems.setText(itemText.toString());
            tvItems.setTextAppearance(getContext(), R.style.TextAppearance_App_Body_Small);

            // Date
            TextView tvDate = new TextView(getContext());
            String createdAt = order.getCreatedAt();
            if (createdAt != null && createdAt.length() >= 16) {
                tvDate.setText(createdAt.substring(0, 16).replace("T", " "));
            }
            tvDate.setTextAppearance(getContext(), R.style.TextAppearance_App_Label_Small);

            content.addView(header);
            content.addView(tvItems);
            content.addView(tvDate);

            card.addView(content);
            layoutRecentOrders.addView(card);
        }
    }

    private List<OrderDto> filterByPeriod(List<OrderDto> orders) {
        if ("all".equals(currentPeriod)) return orders;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if ("week".equals(currentPeriod)) {
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        } else if ("month".equals(currentPeriod)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }

        long cutoff = cal.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        List<OrderDto> filtered = new ArrayList<>();
        for (OrderDto order : orders) {
            String createdAt = order.getCreatedAt();
            if (createdAt == null) continue;

            try {
                Date date = sdf.parse(createdAt);
                if (date != null && date.getTime() >= cutoff) {
                    filtered.add(order);
                }
            } catch (Exception e) {
                // Try a simpler format
                try {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf2.parse(createdAt);
                    if (date != null && date.getTime() >= cutoff) {
                        filtered.add(order);
                    }
                } catch (Exception ignored) {
                    filtered.add(order);
                }
            }
        }
        return filtered;
    }
}
