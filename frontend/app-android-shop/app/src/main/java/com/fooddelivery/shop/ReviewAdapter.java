package com.fooddelivery.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.ReviewDto;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    public interface OnReviewActionListener {
        void onReply(ReviewDto review);
    }

    private List<ReviewDto> reviews = new ArrayList<>();
    private final OnReviewActionListener listener;

    public ReviewAdapter(OnReviewActionListener listener) {
        this.listener = listener;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDto review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUser, tvDate, tvRating, tvContent, tvReplyContent;
        private final LinearLayout layoutReply;
        private final Button btnReply;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_review_user);
            tvDate = itemView.findViewById(R.id.tv_review_date);
            tvRating = itemView.findViewById(R.id.tv_review_rating);
            tvContent = itemView.findViewById(R.id.tv_review_content);
            tvReplyContent = itemView.findViewById(R.id.tv_reply_content);
            layoutReply = itemView.findViewById(R.id.layout_reply);
            btnReply = itemView.findViewById(R.id.btn_reply);
        }

        void bind(ReviewDto review) {
            String userName = review.getUserName();
            if (userName == null || userName.isEmpty()) {
                userName = "고객";
            }
            tvUser.setText(userName);

            String date = review.getCreatedAt();
            if (date != null && date.length() >= 10) {
                tvDate.setText(date.substring(0, 10));
            } else {
                tvDate.setText("");
            }

            // Star rating display
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stars.append(i < review.getRating() ? "★" : "☆");
            }
            tvRating.setText(stars.toString());

            String content = review.getContent();
            tvContent.setText(content != null ? content : "");

            // Reply section
            String reply = review.getReply();
            if (reply != null && !reply.isEmpty()) {
                layoutReply.setVisibility(View.VISIBLE);
                tvReplyContent.setText(reply);
                btnReply.setText("답변 수정");
            } else {
                layoutReply.setVisibility(View.GONE);
                btnReply.setText("답변 작성");
            }

            btnReply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReply(review);
                }
            });
        }
    }
}
