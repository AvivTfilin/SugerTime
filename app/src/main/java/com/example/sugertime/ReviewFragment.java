package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ReviewFragment extends Fragment {


    private static final String TAG = "pttt" + ReviewFragment.class.getName();

    private View rootView;
    private ReviewListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView score;
    private Review review;
    private RecyclerView recyclerView;
    private TextView reviewFragment_LBL_text;


    public static ReviewFragment newInstance() {
        Log.d(TAG, "newInstance");

        ReviewFragment fragment = new ReviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_review_fragment, container, false);
        }

        review = (Review) getArguments().getSerializable("review");

        recyclerView = rootView.findViewById(R.id.reviewFragment_RCV_pictureList);
        score = rootView.findViewById(R.id.reviewFragment_LBL_totalScore);
        reviewFragment_LBL_text = rootView.findViewById(R.id.reviewFragment_LBL_text);

        score.setText("This Shop have in total: " + new DecimalFormat("##.##").format(review.getRating() / review.getNumOfStar()) + " stars");

        if (review.getNumOfReview() != 0) {
            reviewFragment_LBL_text.setVisibility(View.GONE);

            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            adapter = new ReviewListAdapter(review.getReviews());

            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        } else {
            reviewFragment_LBL_text.setVisibility(View.VISIBLE);
        }


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("pttt", "onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }
}