package com.example.sugertime;

import java.io.Serializable;
import java.util.ArrayList;

public class Review implements Serializable {

    private String shopName = "";
    private int numOfReview = 0;
    private int numOfStar = 0;
    private double rating;
    private ArrayList<String> reviews;

    public Review() {

    }

    public Review(String shopName, int numOfReview, int numOfStar, double rating, ArrayList<String> reviews) {
        this.shopName = shopName;
        this.numOfReview = numOfReview;
        this.rating = rating;
        this.reviews = reviews;
        this.numOfStar = numOfStar;
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

    public int getNumOfStar() {
        return numOfStar;
    }

    public void setNumOfStar(int numOfStar) {
        this.numOfStar = numOfStar;
    }
}
