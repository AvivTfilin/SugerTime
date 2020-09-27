package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopPage_screen extends AppCompatActivity {

    private TextView seller_LBL_description;
    private TextView seller_LBL_shopName;
    private DatabaseReference reference;

    private ReviewFragment reviewFragment;

    private Button seller_BTN_update;

    private FloatingActionButton seller_BTN_chat;

    private RecyclePictureAdapter recyclePictureAdapter;

    private RecyclerView seller_RCV_pictureList;
    private Review review;

    private Button seller_BTN_review;
    private LinearLayout seller_LAY_buttons;

    private Shop shop;
    private String owner;

    private boolean isBuyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page_screen);

        findView();
        initButton();

        review = new Review();

        owner = getIntent().getStringExtra("username");
        isBuyer = getIntent().getBooleanExtra("isBuyer",false);

        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shopInfo");

        if(shop != null){

            showShopInfo();
            showReview(shop.getShopName());

            if(isBuyer){
                seller_BTN_review.setVisibility(View.VISIBLE);
                seller_BTN_chat.setVisibility(View.VISIBLE);
                seller_LAY_buttons.setVisibility(View.GONE);
            }else {
                seller_BTN_chat.setVisibility(View.GONE);
                seller_BTN_review.setVisibility(View.GONE);
                seller_LAY_buttons.setVisibility(View.VISIBLE);
            }

        }else {
            shop = new Shop();

            seller_BTN_review.setVisibility(View.GONE);
            seller_LAY_buttons.setVisibility(View.VISIBLE);

            readShopFromDB();
        }


    }

    private void showReview(String shopName) {

        reference = FirebaseDatabase.getInstance().getReference("Reviews/").child(shopName + "/");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                review.setReviews(snapshot.child("reviews/").getValue(t));
                review.setRating(snapshot.child("rating").getValue(double.class));
                review.setNumOfReview(snapshot.child("numOfReview").getValue(int.class));
                review.setShopName(snapshot.child("shopName").getValue(String.class));

                reviewFragment = new ReviewFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("review", review);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.add(R.id.seller_LAY_reviewList, reviewFragment);

                reviewFragment.setArguments(bundle);

                fragmentTransaction.commit();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initButton() {
        seller_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePage();
            }
        });

        seller_BTN_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Review_screen.class);
                intent.putExtra("shop", shop);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updatePage() {
        Intent newIntent = new Intent(getApplicationContext(), Update_screen.class);
        newIntent.putExtra("shopInfo", shop);
        newIntent.putExtra("username",owner);
        startActivity(newIntent);
        finish();
    }


    private void readShopFromDB() {
        reference = FirebaseDatabase.getInstance().getReference("Confectioneries/");

        Query checkUser = reference.orderByChild("owner").equalTo(owner);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    shop.setShopName(snapshot.child(owner).child("shopName").getValue(String.class));
                    shop.setDescription(snapshot.child(owner).child("description").getValue(String.class));
                    shop.setOwner(snapshot.child(owner).child("owner").getValue(String.class));

                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    shop.setImageList(snapshot.child(owner).child("imageList").getValue(t));

                    showShopInfo();
                    showReview(shop.getShopName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showShopInfo() {
        seller_LBL_shopName.setText(shop.getShopName().toUpperCase());
        seller_LBL_description.setText(shop.getDescription());

        showPicture();
    }

    private void showPicture() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        seller_RCV_pictureList.setLayoutManager(linearLayoutManager);
        seller_RCV_pictureList.setItemAnimator(new DefaultItemAnimator());

        recyclePictureAdapter = new RecyclePictureAdapter(getApplicationContext(), shop.getImageList());
        seller_RCV_pictureList.setAdapter(recyclePictureAdapter);
    }

    private void findView() {
        seller_LBL_shopName = findViewById(R.id.seller_LBL_shopName);
        seller_LBL_description = findViewById(R.id.seller_LBL_description);
        seller_RCV_pictureList = findViewById(R.id.seller_RCV_pictureList);
        seller_BTN_update = findViewById(R.id.seller_BTN_update);
        seller_BTN_review = findViewById(R.id.seller_BTN_review);
        seller_LAY_buttons = findViewById(R.id.seller_LAY_buttons);
        seller_BTN_chat = findViewById(R.id.seller_BTN_chat);
    }
}