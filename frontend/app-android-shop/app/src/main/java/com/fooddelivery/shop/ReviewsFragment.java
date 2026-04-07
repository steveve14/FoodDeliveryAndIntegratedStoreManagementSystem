package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.ReplyRequest;
import com.fooddelivery.shop.models.ReviewDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends Fragment {

    private RecyclerView rvReviews;
    private TextView tvEmptyReviews;
    private TextView tvReviewSummary;
    private ReviewAdapter adapter;
    private String storeId;

    private List<ReviewDto> allReviews = new ArrayList<>();
    private int currentFilter = -1; // -1 = all, 0 = no reply, 1-5 = star filter

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvReviews = view.findViewById(R.id.rv_reviews);
        tvEmptyReviews = view.findViewById(R.id.tv_empty_reviews);
        tvReviewSummary = view.findViewById(R.id.tv_review_summary);

        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(this::showReplyDialog);
        rvReviews.setAdapter(adapter);

        // Filter chips
        Chip chipAll = view.findViewById(R.id.chip_all);
        Chip chipStar5 = view.findViewById(R.id.chip_star5);
        Chip chipStar4 = view.findViewById(R.id.chip_star4);
        Chip chipStar3 = view.findViewById(R.id.chip_star3);
        Chip chipStar2 = view.findViewById(R.id.chip_star2);
        Chip chipStar1 = view.findViewById(R.id.chip_star1);
        Chip chipNoReply = view.findViewById(R.id.chip_no_reply);

        View.OnClickListener chipListener = v -> {
            chipAll.setChecked(v == chipAll);
            chipStar5.setChecked(v == chipStar5);
            chipStar4.setChecked(v == chipStar4);
            chipStar3.setChecked(v == chipStar3);
            chipStar2.setChecked(v == chipStar2);
            chipStar1.setChecked(v == chipStar1);
            chipNoReply.setChecked(v == chipNoReply);

            if (v == chipAll) currentFilter = -1;
            else if (v == chipStar5) currentFilter = 5;
            else if (v == chipStar4) currentFilter = 4;
            else if (v == chipStar3) currentFilter = 3;
            else if (v == chipStar2) currentFilter = 2;
            else if (v == chipStar1) currentFilter = 1;
            else if (v == chipNoReply) currentFilter = 0;

            applyFilter();
        };

        chipAll.setOnClickListener(chipListener);
        chipStar5.setOnClickListener(chipListener);
        chipStar4.setOnClickListener(chipListener);
        chipStar3.setOnClickListener(chipListener);
        chipStar2.setOnClickListener(chipListener);
        chipStar1.setOnClickListener(chipListener);
        chipNoReply.setOnClickListener(chipListener);

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
        loadReviews();
    }

    private void loadReviews() {
        if (storeId == null) {
            tvEmptyReviews.setText("먼저 설정에서 로그인해주세요.");
            tvEmptyReviews.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getStoreReviews(storeId).enqueue(new Callback<ApiResponse<List<ReviewDto>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ReviewDto>>> call,
                                   @NonNull Response<ApiResponse<List<ReviewDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allReviews = response.body().getData();
                    if (allReviews == null) allReviews = new ArrayList<>();
                    updateSummary();
                    applyFilter();
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ReviewDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "리뷰 로드 실패", Toast.LENGTH_SHORT).show();
                showEmpty();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateSummary() {
        if (allReviews.isEmpty()) {
            tvReviewSummary.setText("평균 0.0 · 총 0건");
            return;
        }
        double sum = 0;
        for (ReviewDto r : allReviews) {
            sum += r.getRating();
        }
        double avg = sum / allReviews.size();
        tvReviewSummary.setText(String.format("평균 %.1f · 총 %d건", avg, allReviews.size()));
    }

    private void applyFilter() {
        List<ReviewDto> filtered;
        if (currentFilter == -1) {
            filtered = allReviews;
        } else if (currentFilter == 0) {
            filtered = new ArrayList<>();
            for (ReviewDto r : allReviews) {
                if (r.getReply() == null || r.getReply().isEmpty()) {
                    filtered.add(r);
                }
            }
        } else {
            filtered = new ArrayList<>();
            for (ReviewDto r : allReviews) {
                if (r.getRating() == currentFilter) {
                    filtered.add(r);
                }
            }
        }

        if (filtered.isEmpty()) {
            rvReviews.setVisibility(View.GONE);
            tvEmptyReviews.setText("해당 조건의 리뷰가 없습니다");
            tvEmptyReviews.setVisibility(View.VISIBLE);
        } else {
            rvReviews.setVisibility(View.VISIBLE);
            tvEmptyReviews.setVisibility(View.GONE);
        }
        adapter.setReviews(filtered);
    }

    private void showEmpty() {
        allReviews = new ArrayList<>();
        rvReviews.setVisibility(View.GONE);
        tvEmptyReviews.setText("등록된 리뷰가 없습니다");
        tvEmptyReviews.setVisibility(View.VISIBLE);
    }

    private void showReplyDialog(ReviewDto review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(review.getReply() != null && !review.getReply().isEmpty()
                ? "답변 수정" : "답변 작성");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        EditText etReply = new EditText(getContext());
        etReply.setHint("고객에게 답변을 작성해주세요.");
        etReply.setMinLines(3);
        if (review.getReply() != null) {
            etReply.setText(review.getReply());
        }
        layout.addView(etReply);

        builder.setView(layout);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String replyText = etReply.getText().toString().trim();
            if (replyText.isEmpty()) {
                Toast.makeText(getContext(), "답변을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReply(review.getId(), replyText);
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void submitReply(String reviewId, String replyText) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.replyToReview(storeId, reviewId, new ReplyRequest(replyText))
                .enqueue(new Callback<ApiResponse<ReviewDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<ReviewDto>> call,
                                           @NonNull Response<ApiResponse<ReviewDto>> response) {
                        Toast.makeText(getContext(), "답변이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        loadReviews();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<ReviewDto>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "답변 저장 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
