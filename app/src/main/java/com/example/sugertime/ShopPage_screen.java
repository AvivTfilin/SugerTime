package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShopPage_screen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView shop_LBL_description;
    private TextView shop_LBL_shopName;
    private FloatingActionButton shop_BTN_chatWithSeller;
    private Button shop_BTN_review;
    private ImageView shop_IMG_instagram;

    private DrawerLayout shop_LAY_drawerLayout;
    private NavigationView shop_LAY_view;
    private Toolbar shop_TLB_menu;

    private DatabaseReference reference;

    private ReviewFragment reviewFragment;

    private RecyclePictureAdapter recyclePictureAdapter;
    private RecyclerView shop_RCV_pictureList;
    private Review review;

    private Shop shop;
    private String user;

    private boolean isBuyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page_screen);

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        review = new Review();
        shop = new Shop();


        findView();
        initButton();
        menuNavigation();
        readDataFromActivity();

        if (shop != null) {
            showView();
            showShopInfo();
            showReview(shop.getShopName());
        }
    }

    private void initButton() {
        shop_BTN_chatWithSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatWithSeller();
            }
        });

        shop_BTN_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewScreen();
            }
        });

        shop_IMG_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInstagram();
            }
        });
    }

    private void openInstagram() {
        String nickname = shop.getInstagramURL();

        if(nickname != "") {
            Uri uri = Uri.parse("http://instagram.com/_u/" + nickname);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/" + nickname)));
            }
        } else {
            shop_IMG_instagram.setVisibility(View.GONE);
        }

    }

    private void showView() {
        if (isBuyer) {
            shop_TLB_menu.setVisibility(View.GONE);
            shop_BTN_review.setVisibility(View.VISIBLE);
            shop_BTN_chatWithSeller.setVisibility(View.VISIBLE);
        } else {
            shop_BTN_chatWithSeller.setVisibility(View.GONE);
            shop_BTN_review.setVisibility(View.GONE);
        }
    }

    // // Get information from previous activity
    private void readDataFromActivity() {
        isBuyer = getIntent().getBooleanExtra("isBuyer", false);

        Bundle data = getIntent().getExtras();
        assert data != null;
        shop = (Shop) data.getSerializable("shopInfo");
    }

    // Setting menu navigation
    private void menuNavigation() {
        shop_LAY_view.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, shop_LAY_drawerLayout, shop_TLB_menu, R.string.menu_open, R.string.menu_close);

        shop_LAY_drawerLayout.addDrawerListener(toggle);
        shop_LAY_view.setNavigationItemSelectedListener(this);
        toggle.syncState();
    }

    // Display on screen shop details
    private void showShopInfo() {
        shop_LBL_shopName.setText(shop.getShopName().toUpperCase());
        shop_LBL_description.setText(shop.getDescription());

        showPicture();
    }

    // Show all store picture in recycle view
    private void showPicture() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        shop_RCV_pictureList.setLayoutManager(linearLayoutManager);
        shop_RCV_pictureList.setItemAnimator(new DefaultItemAnimator());

        if (shop.getImageList() != null) {
            recyclePictureAdapter = new RecyclePictureAdapter(getApplicationContext(), shop.getImageList());
            shop_RCV_pictureList.setAdapter(recyclePictureAdapter);
        }
    }

    // Read review about the store from DB
    private void showReview(String shopName) {
        reference = FirebaseDatabase.getInstance().getReference("Reviews/").child(shopName + "/");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                review = snapshot.getValue(Review.class);

                // Show review about the store in recycle view
                reviewFragment = new ReviewFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("review", review);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.add(R.id.shop_LAY_reviewList, reviewFragment);

                reviewFragment.setArguments(bundle);

                fragmentTransaction.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatWithSeller() {
        Intent intent = new Intent(getApplicationContext(), Chat_screen.class);

        intent.putExtra("sender", user);
        intent.putExtra("sendTo", shop.getOwner());

        startActivity(intent);
    }

    private void reviewScreen() {
        Intent intent = new Intent(getApplicationContext(), Review_screen.class);

        intent.putExtra("shopInfo", shop);
        intent.putExtra("user", user);

        startActivity(intent);
        finish();
    }

    private void updatePage() {
        Intent newIntent = new Intent(getApplicationContext(), Update_screen.class);

        newIntent.putExtra("shopInfo", shop);
        startActivity(newIntent);

        finish();
    }

    private void findView() {
        shop_LBL_shopName = findViewById(R.id.shop_LBL_shopName);
        shop_LBL_description = findViewById(R.id.shop_LBL_description);
        shop_RCV_pictureList = findViewById(R.id.shop_RCV_pictureList);
        shop_BTN_review = findViewById(R.id.shop_BTN_review);
        shop_BTN_chatWithSeller = findViewById(R.id.shop_BTN_chatWithSeller);
        shop_LAY_drawerLayout = findViewById(R.id.shop_LAY_drawerLayout);
        shop_LAY_view = findViewById(R.id.shop_LAY_view);
        shop_TLB_menu = findViewById(R.id.shop_TLB_menu);
        shop_IMG_instagram = findViewById(R.id.shop_IMG_instagram);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.main_NAV_logOut:
                intent = new Intent(getApplicationContext(), Login_screen.class);
                startActivity(intent);
                finish();
                break;

            case R.id.main_NAV_chatList:
                intent = new Intent(getApplicationContext(), Chats_list_screen.class);
                intent.putExtra("user", shop.getOwner());
                startActivity(intent);
                break;

            case R.id.main_NAV_update:
                updatePage();
                break;

        }

        return true;
    }
}