package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
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
    private TextInputLayout create_LAY_instagram;
    private Button create_BTN_submit;
    private ImageView create_IMG_image;

    private ShopLocation shopLocation;
    private CheckInputValue checkInputValue;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String user;
    private Shop shop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_screen);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        checkInputValue = new CheckInputValue();
        shopLocation = new ShopLocation();

        user = mAuth.getCurrentUser().getUid();

        findView();
        initButton();
    }

    private void initButton() {
        create_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveShopInDB();
            }
        });
    }

    // Finds the location of the store on the map by the address entered by the user
    private void saveShopAddressInDB() {
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(create_LAY_address.getEditText().getText().toString(), 5);
            if (address == null) {
                create_LAY_address.setError("NOT FOUND LOCATION");
            }

            Address location = address.get(0);

            shopLocation.setLon(location.getLongitude());
            shopLocation.setLat(location.getLatitude());

            saveLocationInDB();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save store location in DB
    private void saveLocationInDB() {
        mDatabase.child("ShopLocation//").child(user + "/").setValue(shopLocation);
    }

    // Save store in DB
    private void saveShopInDB() {
        // Checks if the data entered by user is logically correct
        checkInputValue();
        // Add to DB if The name of shop is not exist
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
                    shopLocation.setOwner(user);

                    addShopToDB();
                    saveShopAddressInDB();
                    newActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void newActivity() {
        Intent intent = new Intent(getApplicationContext(), Update_screen.class);
        intent.putExtra("shopInfo", shop);
        startActivity(intent);
        finish();
    }

    private void addShopToDB() {
        ArrayList<String> imageList = new ArrayList<>();
        shop = new Shop(create_LAY_storeName.getEditText().getText().toString(), "",
                user, imageList, create_LAY_instagram.getEditText().getText().toString());

        // Save store in DB
        mDatabase.child("Confectioneries/").child(shop.getOwner()).setValue(shop);
        mDatabase.child("Users").child(shop.getOwner()).child("shopName").setValue(shop.getShopName());

        // Create review in DB for this store
        createReviewForThisShop(create_LAY_storeName.getEditText().getText().toString());
    }

    private void createReviewForThisShop(String shopName) {
        ArrayList<String> listReview = new ArrayList<>();
        Review review = new Review(shopName, 0, 0, 0, listReview);

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
        create_LAY_instagram = findViewById(R.id.create_LAY_instagram);
        create_IMG_image.setImageResource(R.drawable.ic_build);
    }
}