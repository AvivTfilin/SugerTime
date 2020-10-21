package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_screen extends AppCompatActivity {

    private Button login_BTN_signUp;
    private Button login_BTN_logIn;
    private ImageView login_IMG_loginImage;
    private ImageView login_IMG_singUpImage;
    private RelativeLayout login_LAY_loading;

    private FirebaseAuth mAuth;

    private TextInputLayout logIn_LAY_email;
    private TextInputLayout logIn_LAY_password;

    private CheckInputValue checkInputValue;

    private final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        fetchLocation();

        checkInputValue = new CheckInputValue();
        mAuth = FirebaseAuth.getInstance();

        findView();
        initButton();

        login_LAY_loading.setVisibility(View.GONE);
        login_IMG_loginImage.setImageResource(R.drawable.ic_signin);
        login_IMG_singUpImage.setImageResource(R.drawable.ic_addpeople);
    }

    private void initButton() {
        login_BTN_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_screen.this, SignUp_screen.class);
                startActivity(intent);
            }
        });

        login_BTN_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserInput();
            }
        });
    }

    // Checks if the data entered by user is logically correct
    private void checkUserInput() {
        if (!checkInputValue.validateEmail(logIn_LAY_email) | !checkInputValue.validatePassword(logIn_LAY_password)) {
            return;
        } else {
            isUserExist();
        }
    }

    // Checks if a user is registered in the system
    private void isUserExist() {
        final String userEnteredEmail = logIn_LAY_email.getEditText().getText().toString().trim();
        final String userEnteredPassword = logIn_LAY_password.getEditText().getText().toString().trim();

        login_LAY_loading.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If user exist , we read his data from DB
                            FirebaseUser user = mAuth.getCurrentUser();
                            readUserFromDB(user.getUid());
                        } else {
                            // If the system is unable to find the user, it issues an error message accordingly
                            login_LAY_loading.setVisibility(View.GONE);

                            if (task.getException().getMessage().contains("email")) {
                                logIn_LAY_email.setError(task.getException().getMessage());
                            } else if (task.getException().getMessage().contains("password")) {
                                logIn_LAY_password.setError(task.getException().getMessage());
                            } else if (task.getException().getMessage().contains("no user")) {
                                logIn_LAY_email.setError(task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    // Read user data from DB
    void readUserFromDB(final String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                newActivity(user, userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void newActivity(User user, final String userID) {
        Intent intent;
        // If the user is a seller, we will read from the DB the information about his store
        if (user.getRole().equals("Seller")) {
            readShopFromDB(userID);
        } else {
            intent = new Intent(getApplicationContext(), Buyer_screen.class);
            startActivity(intent);
        }
    }

    // Read user store from DB
    private void readShopFromDB(final String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Confectioneries/").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Shop shop = snapshot.getValue(Shop.class);
                Intent intent = new Intent(getApplicationContext(), ShopPage_screen.class);

                intent.putExtra("shopInfo", shop);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Has the user given permissions
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void findView() {
        login_BTN_signUp = findViewById(R.id.login_BTN_signup);
        login_BTN_logIn = findViewById(R.id.login_BTN_logIn);
        logIn_LAY_email = findViewById(R.id.logIn_LAY_email);
        logIn_LAY_password = findViewById(R.id.logIn_LAY_password);
        login_IMG_loginImage = findViewById(R.id.login_IMG_loginImage);
        login_IMG_singUpImage = findViewById(R.id.login_IMG_singUpImage);
        login_LAY_loading = findViewById(R.id.login_LAY_loading);
    }
}