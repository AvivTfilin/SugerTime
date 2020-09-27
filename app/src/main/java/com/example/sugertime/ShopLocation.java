package com.example.sugertime;

public class ShopLocation {

    private String shopName = "";
    private String owner = "";
    private double lat = 0;
    private double lon = 0;

    public  ShopLocation() {

    }

    public ShopLocation(String shopName, String owner, double lat, double lon) {
        this.shopName = shopName;
        this.owner = owner;
        this.lat = lat;
        this.lon = lon;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
