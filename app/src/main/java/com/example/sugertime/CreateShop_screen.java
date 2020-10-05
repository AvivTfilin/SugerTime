package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateShop_screen extends AppCompatActivity {

    private TextInputLayout create_LAY_storeName;
    private TextInputLayout create_LAY_address;
    private Button create_BTN_submit;

    private String username;
    private ShopLocation shopLocation;
    private CheckInputValue checkInputValue;
    private DatabaseReference mDatabase;

    private ImageView create_IMG_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_screen);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        checkInputValue = new CheckInputValue();
        shopLocation = new ShopLocation();

        findView();
        initButton();

        create_IMG_image.setImageResource(R.drawable.ic_build);


    }

    private void initButton() {
        create_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveShopNameAndShopLocation();
            }
        });
    }

    private void saveShopNameAndShopLocation() {
        saveShopInDB();

    }

    private void saveShopAddressInDB() {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(create_LAY_address.getEditText().getText().toString(),5);
            if (address == null ){
                create_LAY_address.setError("NOT FOUND LOCATION");
            }

            Address location=address.get(0);

            shopLocation.setLon(location.getLongitude());
            shopLocation.setLat(location.getLatitude());

            saveLocationInDB();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLocationInDB() {
        mDatabase.child("ShopLocation//").child(shopLocation.getOwner() + "/").setValue(shopLocation);
    }

    private void saveShopInDB() {
        checkInputValue();
        checkIfStoreNameExistAndAddToDB();
    }

    private void checkIfStoreNameExistAndAddToDB() {
        Query checkUser = mDatabase.child("Confectioneries/").orderByChild("shopName").equalTo(create_LAY_storeName.getEditText().getText().toString());

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    create_LAY_storeName.setError("A SHOP name already exists in the system");
                } else {
                    create_LAY_storeName.setError(null);
                    create_LAY_storeName.setErrorEnabled(false);

                    shopLocation.setShopName(create_LAY_storeName.getEditText().getText().toString());
                    shopLocation.setOwner(username);

                    addShopToDB();
                    saveShopAddressInDB();

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addShopToDB() {

        ArrayList<String> imageList = new ArrayList<>();
        Shop shop = new Shop(create_LAY_storeName.getEditText().getText().toString(), "", username, imageList);

        mDatabase.child("Confectioneries/").child(shop.getOwner()).setValue(shop);

        createReviewForThisShop(create_LAY_storeName.getEditText().getText().toString());

    }

    private void createReviewForThisShop(String shopName){

        ArrayList<String> listReview = new ArrayList<>();

        Review review = new Review(shopName, 0,0, 0, listReview);

        mDatabase.child("Reviews/").child(review.getShopName()).setValue(review);

    }

    private void checkInputValue() {
        if (!checkInputValue.validateName(create_LAY_storeName) | !checkInputValue.validateName(create_LAY_address)) {
            return;
        }
    }

    private void findView() {
        create_LAY_storeName = findViewById(R.id.create_LAY_storeName);
        create_LAY_address = findViewById(R.id.create_LAY_address);
        create_BTN_submit = findViewById(R.id.create_BTN_submit);
        create_IMG_image = findViewById(R.id.create_IMG_image);
    }
}