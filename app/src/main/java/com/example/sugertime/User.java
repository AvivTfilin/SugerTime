package com.example.sugertime;

import java.util.UUID;

public class User {
    private String fullName = "";
    private String userName = "";
    private String email = "";
    private String role = "";
    private String password = "";
    private boolean createPage = false;

    public User(){
    }

    public User(String fullName, String userName, String email, String role, String password, boolean createPage) {
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.createPage = createPage;
    }


    public String getFullName() {
        return fullName;
    }

    public boolean isCreatePage() {
        return createPage;
    }

    public void setCreatePage(boolean createPage) {
        this.createPage = createPage;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
