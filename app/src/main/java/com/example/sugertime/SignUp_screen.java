package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp_screen extends AppCompatActivity {

    private TextInputLayout signUp_LAY_fullName;
    private TextInputLayout signUp_LAY_userName;
    private TextInputLayout signUp_LAY_email;
    private TextInputLayout signUp_LAY_role;
    private TextInputLayout signUp_LAY_password;

    private Button signUp_BTN_submit;
    private Button signUp_BTN_back;

    private CheckInputValue checkInputValue;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        checkInputValue = new CheckInputValue();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/");

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

        checkIfUsernameExist();
    }

    private void checkIfUsernameExist() {

        Query checkUser = mDatabase.orderByChild("userName").equalTo(signUp_LAY_userName.getEditText().getText().toString());

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    signUp_LAY_userName.setError("A USERNAME already exists in the system");
                } else {
                    signUp_LAY_userName.setError(null);
                    signUp_LAY_userName.setErrorEnabled(false);

                    addUser();

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addUser() {
        User user = new User(signUp_LAY_fullName.getEditText().getText().toString(), signUp_LAY_userName.getEditText().getText().toString(),
                signUp_LAY_email.getEditText().getText().toString(), signUp_LAY_role.getEditText().getText().toString(),
                signUp_LAY_password.getEditText().getText().toString(),false);

        mDatabase.child(user.getUserName()).setValue(user);
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