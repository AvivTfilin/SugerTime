package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Review_screen extends AppCompatActivity {

    private RatingBar review_RTB_rating;
    private EditText review_LBL_buyerReview;
    private Button review_BTN_submit;
    private ImageView review_IMG_back;
    private DatabaseReference mDatabase;
    private Shop shop;

    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_screen);

        mDatabase = FirebaseDatabase.getInstance().getReference("Reviews/");



        Intent intent = getIntent();
        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shop");

        findView();
        initButton();

        mDatabase = FirebaseDatabase.getInstance().getReference("Reviews/").child(shop.getShopName() + "/");

        review = new Review();

        review_IMG_back.setImageResource(R.drawable.ic_back);


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

    private void saveData(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               double rating =  snapshot.child("rating/").getValue(double.class);
               int numOfReview = snapshot.child("numOfReview/").getValue(int.class);


                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> reviewsArray = snapshot.child("reviews/").getValue(t);


               updateDataInDB(rating, numOfReview, reviewsArray);
               returnToBuyerScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void returnToBuyerScreen() {
        Intent intent = new Intent(getApplicationContext(), Seller_screen.class);
        intent.putExtra("shopInfo", shop);
        intent.putExtra("isBuyer",true);
        startActivity(intent);
        finish();

    }

    private void updateDataInDB(double rating, int nunOfReview, ArrayList<String> reviewsArray) {

        mDatabase.child("reviews/").child("" + nunOfReview).setValue(review_LBL_buyerReview.getText().toString());

        Log.d("hello", "rating: " + rating + "num of review: " + nunOfReview);

        nunOfReview++;

        mDatabase.child("rating/").setValue((rating + review_RTB_rating.getRating()));
        mDatabase.child("numOfReview/").setValue(nunOfReview);

    }


    private void findView() {
        review_RTB_rating = findViewById(R.id.review_RTB_rating);
        review_LBL_buyerReview = findViewById(R.id.review_LBL_buyerReview);
        review_BTN_submit = findViewById(R.id.review_BTN_submit);
        review_IMG_back = findViewById(R.id.review_IMG_back);

    }
}