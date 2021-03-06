package com.example.sugertime;

import java.io.Serializable;
import java.util.ArrayList;

public class Shop implements Serializable {

    private String shopName = "";
    private String description = "";
    private String owner = "";
    private String instagramURL = "";
    private ArrayList<String> imageList;

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public Shop() {

    }

    public Shop(String shopName, String description, String owner, ArrayList<String> imageList, String instagramURL) {
        this.shopName = shopName;
        this.description = description;
        this.owner = owner;
        this.imageList = imageList;
        this.instagramURL = instagramURL;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getInstagramURL() {
        return instagramURL;
    }

    public void setInstagramURL(String instagramURL) {
        this.instagramURL = instagramURL;
    }
}
