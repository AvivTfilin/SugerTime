package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Login_screen extends AppCompatActivity {

    private Button login_BTN_signUp;
    private Button login_BTN_logIn;

    private TextInputLayout logIn_LAY_username;
    private TextInputLayout logIn_LAY_password;

    private CheckInputValue checkInputValue;

    private ImageView login_IMG_loginImage;
    private ImageView login_IMG_singUpImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        checkInputValue = new CheckInputValue();

        findView();
        initButton();

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

    private void checkUserInput() {

        if (!checkInputValue.validateUserName(logIn_LAY_username) | !checkInputValue.validatePassword(logIn_LAY_password)) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {

        final String userEnteredUsername = logIn_LAY_username.getEditText().getText().toString().trim();
        final String userEnteredPassword = logIn_LAY_password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/");
        Query checkUser = reference.orderByChild("userName").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    logIn_LAY_username.setError(null);
                    logIn_LAY_username.setErrorEnabled(false);
                    String passwordFromDB = snapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword)) {
                        logIn_LAY_password.setError(null);
                        logIn_LAY_password.setErrorEnabled(false);

                        goToNewActivity(snapshot.child(userEnteredUsername).child("role").getValue(String.class),
                                snapshot.child(userEnteredUsername).child("createPage").getValue(boolean.class),
                                userEnteredUsername);

                    } else {
                        logIn_LAY_password.setError("Wrong Password");
                        logIn_LAY_password.requestFocus();
                    }
                } else {
                    logIn_LAY_username.setError("No such User exist");
                    logIn_LAY_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToNewActivity(String clientType, boolean createPage, String username) {
        Intent intent;
        if(clientType.equals("Seller")){
            if (createPage){
                intent = new Intent(getApplicationContext(), ShopPage_screen.class);
            } else {
                intent = new Intent(getApplicationContext(), Update_screen.class);
            }

        }
        else {
            intent = new Intent(getApplicationContext(), Buyer_screen.class);
        }

        intent.putExtra("username", username);

        startActivity(intent);
        finish();
    }

    private void findView() {
        login_BTN_signUp = findViewById(R.id.login_BTN_signup);
        login_BTN_logIn = findViewById(R.id.login_BTN_logIn);
        logIn_LAY_username = findViewById(R.id.logIn_LAY_username);
        logIn_LAY_password = findViewById(R.id.logIn_LAY_password);
        login_IMG_loginImage = findViewById(R.id.login_IMG_loginImage);
        login_IMG_singUpImage = findViewById(R.id.login_IMG_singUpImage);
    }
}