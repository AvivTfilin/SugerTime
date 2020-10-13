package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Review_screen extends AppCompatActivity {

    private RatingBar review_RTB_rating;
    private EditText review_LBL_buyerReview;
    private Button review_BTN_submit;
    private ImageView review_IMG_back;
    private ImageView review_IMG_image;

    private DatabaseReference mDatabase;

    private Intent intent;

    private Shop shop;
    private String user;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_screen);

        intent = getIntent();
        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shopInfo");

        user = intent.getStringExtra("user");

        findView();
        initButton();

        review_IMG_image.setImageResource(R.drawable.ic_bakery);
        review_IMG_back.setImageResource(R.drawable.ic_back);

        mDatabase = FirebaseDatabase.getInstance().getReference("Reviews/").child(shop.getShopName() + "/");

        review = new Review();
    }

    private void initButton() {
        review_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        review_IMG_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToBuyerScreen();
            }
        });
    }

    // Read review about the store and Update store review in DB
    private void saveData() {
        // Read all review about the store from DB
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double rating = snapshot.child("rating/").getValue(double.class);
                int numOfStar = snapshot.child("numOfStar/").getValue(int.class);
                int numOfReview = snapshot.child("numOfReview/").getValue(int.class);

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };
                ArrayList<String> reviewsArray = snapshot.child("reviews/").getValue(t);

                updateDataInDB(rating, numOfStar, numOfReview);
                returnToBuyerScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void returnToBuyerScreen() {

        Intent intent = new Intent(getApplicationContext(), ShopPage_screen.class);
        intent.putExtra("shopInfo", shop);
        intent.putExtra("isBuyer", true);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();

    }

    // Update the review and information about the store and save it in DB
    private void updateDataInDB(double rating, int numOfStar, int numOfReview) {

        if (!review_LBL_buyerReview.getText().toString().equals("")) {
            mDatabase.child("reviews/").child("" + numOfReview).setValue(review_LBL_buyerReview.getText().toString());

            numOfReview++;

            mDatabase.child("numOfReview/").setValue(numOfReview);
        }

        numOfStar++;

        mDatabase.child("rating/").setValue((rating + review_RTB_rating.getRating()));
        mDatabase.child("numOfStar/").setValue(numOfStar);

    }


    private void findView() {
        review_RTB_rating = findViewById(R.id.review_RTB_rating);
        review_LBL_buyerReview = findViewById(R.id.review_LBL_buyerReview);
        review_BTN_submit = findViewById(R.id.review_BTN_submit);
        review_IMG_back = findViewById(R.id.review_IMG_back);
        review_IMG_image = findViewById(R.id.review_IMG_image);

    }
}