package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopPage_screen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView seller_LBL_description;
    private TextView seller_LBL_shopName;
    private DatabaseReference reference;

    private ReviewFragment reviewFragment;

    private Button seller_BTN_update;
    private Button shop_BTN_chatList;

    private FloatingActionButton seller_BTN_chatWithSeller;

    private RecyclePictureAdapter recyclePictureAdapter;

    private RecyclerView seller_RCV_pictureList;
    private Review review;

    private Button seller_BTN_review;
    private LinearLayout seller_LAY_buttons;

    private Shop shop;
    private String owner;
    private String user;

    private boolean isBuyer;


    private DrawerLayout shop_LAY_drawerLayout;
    private NavigationView shop_LAY_view;
    private Toolbar shop_TLB_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page_screen);

        user = getIntent().getStringExtra("user");

        findView();
        initButton();


        /////
        shop_LAY_view.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, shop_LAY_drawerLayout, shop_TLB_menu, R.string.menu_open, R.string.menu_close);

        shop_LAY_drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        shop_LAY_view.setNavigationItemSelectedListener(this);

        ///
        review = new Review();

        owner = getIntent().getStringExtra("username");
        isBuyer = getIntent().getBooleanExtra("isBuyer", false);

        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shopInfo");

        if (shop != null) {

            showShopInfo();
            showReview(shop.getShopName());

            if (isBuyer) {
                shop_TLB_menu.setVisibility(View.GONE);
                seller_BTN_review.setVisibility(View.VISIBLE);
                seller_BTN_chatWithSeller.setVisibility(View.VISIBLE);
                seller_LAY_buttons.setVisibility(View.GONE);
            } else {
                seller_BTN_chatWithSeller.setVisibility(View.GONE);
                seller_BTN_review.setVisibility(View.GONE);
                seller_LAY_buttons.setVisibility(View.VISIBLE);
            }

        } else {
            shop = new Shop();
            seller_BTN_chatWithSeller.setVisibility(View.GONE);
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
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };
                review.setReviews(snapshot.child("reviews/").getValue(t));
                review.setRating(snapshot.child("rating").getValue(double.class));
                review.setNumOfReview(snapshot.child("numOfReview").getValue(int.class));
                review.setShopName(snapshot.child("shopName").getValue(String.class));
                review.setNumOfStar(snapshot.child("numOfStar").getValue(int.class));

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

        seller_BTN_chatWithSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Chat_screen.class);
                Log.d("hello", user);
                intent.putExtra("sender", user);
                intent.putExtra("sendTo", shop.getOwner());

                startActivity(intent);
            }
        });

        seller_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePage();
            }
        });

        shop_BTN_chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Chats_list_screen.class);
                intent.putExtra("user", shop.getOwner());
                startActivity(intent);
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
        newIntent.putExtra("username", owner);
        startActivity(newIntent);
        finish();
    }


    private void readShopFromDB() {
        reference = FirebaseDatabase.getInstance().getReference("Confectioneries/");

        Query checkUser = reference.orderByChild("owner").equalTo(owner);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    shop.setShopName(snapshot.child(owner).child("shopName").getValue(String.class));
                    shop.setDescription(snapshot.child(owner).child("description").getValue(String.class));
                    shop.setOwner(snapshot.child(owner).child("owner").getValue(String.class));

                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
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
        seller_BTN_chatWithSeller = findViewById(R.id.seller_BTN_chatWithSeller);
        shop_BTN_chatList = findViewById(R.id.shop_BTN_chatList);


        shop_LAY_drawerLayout = findViewById(R.id.shop_LAY_drawerLayout);
        shop_LAY_view = findViewById(R.id.shop_LAY_view);
        shop_TLB_menu = findViewById(R.id.shop_TLB_menu);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.main_NAV_logOut){
            Intent intent = new Intent(getApplicationContext(), Login_screen.class);
            startActivity(intent);
            finish();
        }

        return true;
    }
}