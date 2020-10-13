package com.example.sugertime;

import android.text.Editable;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

public class CheckInputValue {

    public boolean validateName(TextInputLayout name) {
        String val = name.getEditText().getText().toString();

        if(val.isEmpty()){
            name.setError("Field cannot be empty");
            return false;
        }
        else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateUserName(TextInputLayout username) {
        String val = username.getEditText().getText().toString();

        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }
        else if(val.length() >= 15){
            username.setError("Username too long");
            return false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateEmail(TextInputLayout email) {
        String val = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()){
            email.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(emailPattern)){
            email.setError("Invalid email address");
            return false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateRole(TextInputLayout role) {

        String val = role.getEditText().getText().toString();

        if(val.isEmpty()){
            role.setError("Field cannot be empty");
            return false;
        }
        else {
            role.setError(null);
            role.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validatePassword(TextInputLayout password) {
        String val = password.getEditText().getText().toString();

        if(val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        }
        else if(val.length() < 6) {
            password.setError("Password too short, you need more " + (6-val.length() + " char"));
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }



}
