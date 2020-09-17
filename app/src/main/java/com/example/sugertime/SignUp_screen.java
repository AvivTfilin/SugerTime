package com.example.sugertime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class SignUp_screen extends AppCompatActivity {

    private TextInputLayout signUp_LAY_fullName;
    private TextInputLayout signUp_LAY_userName;
    private TextInputLayout signUp_LAY_email;
    private TextInputLayout signUp_LAY_role;
    private TextInputLayout signUp_LAY_password;

    private Button signUp_BTN_submit;
    private Button signUp_BTN_back;

    private CheckInputValue checkInputValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        checkInputValue = new CheckInputValue();

        setDropDown();
        findView();
        initButton();

    }

    private void initButton() {
        signUp_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        signUp_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValueAndStore();
            }
        });
    }

    private void checkValueAndStore() {
        if(!checkInputValue.validateName(signUp_LAY_fullName) | !checkInputValue.validateUserName(signUp_LAY_userName) |
                !checkInputValue.validateEmail(signUp_LAY_email) | !checkInputValue.validateRole(signUp_LAY_role) |
                !checkInputValue.validatePassword(signUp_LAY_password)){
            return;
        }
    }

    private void findView() {
        signUp_LAY_fullName = findViewById(R.id.signUp_LAY_fullName);
        signUp_LAY_userName = findViewById(R.id.signUp_LAY_userName);
        signUp_LAY_email = findViewById(R.id.signUp_LAY_email);
        signUp_LAY_role = findViewById(R.id.signUp_LAY_role);
        signUp_LAY_password = findViewById(R.id.signUp_LAY_password);

        signUp_BTN_submit = findViewById(R.id.signUp_BTN_submit);
        signUp_BTN_back = findViewById(R.id.signUp_BTN_back);
    }

    private void setDropDown() {
        String[] ROLES = new String[]{"Seller","Buyer"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item,ROLES);
        AutoCompleteTextView editTextFillExposedDropdown = (AutoCompleteTextView) findViewById(R.id.signUp_LBL_role);
        editTextFillExposedDropdown.setAdapter(adapter);
    }
}