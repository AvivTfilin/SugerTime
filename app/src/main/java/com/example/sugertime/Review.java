package com.example.sugertime;

import java.io.Serializable;
import java.util.ArrayList;

public class Review implements Serializable {

    private String shopName = "";
    private int numOfReview = 0;
    private double rating;
    private ArrayList<String> reviews;

    public Review() {

    }

    public Review(String shopName, int numOfReview, double rating, ArrayList<String> reviews) {
        this.shopName = shopName;
        this.numOfReview = numOfReview;
        this.rating = rating;
        this.reviews = reviews;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getNumOfReview() {
        return numOfReview;
    }

    public void setNumOfReview(int numOfReview) {
        this.numOfReview = numOfReview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}
