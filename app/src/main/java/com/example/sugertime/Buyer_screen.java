package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Buyer_screen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private DatabaseReference reference;

    private HashMap<String, String> shopOwner;
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_screen);

        shopOwner = new HashMap<>();

        findBuyerLocation();

    }

    private void findBuyerLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();

            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        currentLocation = location;

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(Buyer_screen.this);
                    }
                }
            });
        } else {
            //TODO : add toast message
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final int ZOOM = 15;
        mMap = googleMap;


        reference = FirebaseDatabase.getInstance().getReference("ShopLocation/");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                   String shopName = ds.child("shopName").getValue(String.class);
                   double lat = ds.child("lat").getValue(double.class);
                   double lon = ds.child("lon").getValue(double.class);
                   String owner = ds.child("owner").getValue(String.class);

                   shopOwner.put(shopName, owner);

                   mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(shopName)).showInfoWindow();

                   clickMarker(mMap);

                   mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                       @Override
                       public void onMapClick(LatLng latLng) {

                       }
                   });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LatLng buyerLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(buyerLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buyerLocation, ZOOM));

    }

    private void clickMarker(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();
                String owner = shopOwner.get(markerTitle);


                reference = FirebaseDatabase.getInstance().getReference("Confectioneries/").child(owner + "/");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shop = new Shop();

                        shop.setShopName(snapshot.child("shopName").getValue(String.class));
                        shop.setDescription(snapshot.child("description").getValue(String.class));
                        shop.setOwner(snapshot.child("owner").getValue(String.class));

                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        shop.setImageList(snapshot.child("imageList").getValue(t));

                        Intent intent = new Intent(getApplicationContext(), Seller_screen.class);
                        intent.putExtra("shopInfo", shop);
                        intent.putExtra("isBuyer", true);

                        startActivity(intent);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






                return false;
            }
        });
    }
}