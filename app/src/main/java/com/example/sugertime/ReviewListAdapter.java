package com.example.sugertime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolderList> {
    ArrayList<String> reviewList;


    public ReviewListAdapter(ArrayList<String> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        final ReviewListAdapter.ViewHolderList viewHolder = new ReviewListAdapter.ViewHolderList(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderList holder, int position) {
        holder.review_LBL_reviewText.setText(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        private TextView review_LBL_reviewText;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);

            review_LBL_reviewText = itemView.findViewById(R.id.review_LBL_reviewText);
        }
    }
}
