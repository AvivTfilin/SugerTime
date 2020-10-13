package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private FirebaseAuth mAuth;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        checkInputValue = new CheckInputValue();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/");
        mAuth = FirebaseAuth.getInstance();

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

    // Checks if the data entered by user is logically correct
    private void checkValueAndStore() {
        if (!checkInputValue.validateName(signUp_LAY_fullName) | !checkInputValue.validateUserName(signUp_LAY_userName) |
                !checkInputValue.validateEmail(signUp_LAY_email) | !checkInputValue.validateRole(signUp_LAY_role) |
                !checkInputValue.validatePassword(signUp_LAY_password)) {
            return;
        }

        createUser();

        String email = signUp_LAY_email.getEditText().getText().toString().trim();
        String password = signUp_LAY_password.getEditText().getText().toString().trim();

        saveUserInDB(email, password);
    }

    private void saveUserInDB(final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // When the data entered by the user is correct
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Save user in DB
                            addNewUser(user);
                            newActivity();
                        } else {
                            if (task.getException().getMessage().contains("email")) {
                                signUp_LAY_email.setError(task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    private void newActivity() {
        Intent intent;
        // If user select his role seller , we go to create shop activity
        if (user.getRole().equals("Seller")) {
            intent = new Intent(getApplicationContext(), CreateShop_screen.class);
        } else {
            intent = new Intent(getApplicationContext(), Buyer_screen.class);
        }

        startActivity(intent);
        finish();
    }

    // Save user In DB
    private void addNewUser(FirebaseUser firebaseUser) {
        String keyId = mAuth.getCurrentUser().getUid();
        mDatabase.child(keyId).setValue(user);
    }

    private void createUser() {
        user = new User(signUp_LAY_fullName.getEditText().getText().toString(), signUp_LAY_userName.getEditText().getText().toString(),
                signUp_LAY_email.getEditText().getText().toString(), signUp_LAY_role.getEditText().getText().toString(),
                signUp_LAY_password.getEditText().getText().toString(), "");
    }

    // Create option to drop down
    private void setDropDown() {
        String[] ROLES = new String[]{"Seller", "Buyer"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, ROLES);
        AutoCompleteTextView editTextFillExposedDropdown = (AutoCompleteTextView) findViewById(R.id.signUp_LBL_role);
        editTextFillExposedDropdown.setAdapter(adapter);
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
}