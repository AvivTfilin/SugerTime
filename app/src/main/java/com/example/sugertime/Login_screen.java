package com.example.sugertime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login_screen extends AppCompatActivity {

    private Button login_BTN_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        findView();

        login_BTN_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_screen.this, SignUp_screen.class);
                startActivity(intent);
            }
        });


    }

    private void findView() {
        login_BTN_signup = findViewById(R.id.login_BTN_signup);
    }
}