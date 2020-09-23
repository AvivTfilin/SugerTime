package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Seller_screen extends AppCompatActivity {

    private TextView seller_LBL_description;
    private TextView seller_LBL_shopName;
    private DatabaseReference reference;

    private Button seller_BTN_update;

    private RecyclePictureAdapter recyclePictureAdapter;

    private RecyclerView seller_RCV_pictureList;

    private Shop shop;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_screen);

        findView();
        initButton();

        owner = getIntent().getStringExtra("username");
        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shopInfo");
        
        if(shop != null){
            showShopInfo();
        }else {
            shop = new Shop();
            readShopFromDB();
        }
    }

    private void initButton() {
        seller_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePage();
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
    }
}