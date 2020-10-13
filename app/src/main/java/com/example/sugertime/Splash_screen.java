package com.example.sugertime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_screen extends AppCompatActivity {

    private ImageView splash_IMG_imageDisplay;
    private TextView splash_LBL_title;

    private Animation splash_top_animation;
    private Animation splash_bottom_Animation;

    private LocationManager locationManager;

    private final int REQUEST_LOCATION = 1;
    private final int SPLASH_SCREEN = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splash_top_animation = AnimationUtils.loadAnimation(this, R.anim.splash_top_animation);
        splash_bottom_Animation = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_animation);

        findView();
        initAnimation();

        splash_IMG_imageDisplay.setImageResource(R.drawable.ic_doughnut);

        checkIfGPSOn();

        handlerSplashScreen();
    }

    private void handlerSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_screen.this, Login_screen.class);

                Pair pair = new Pair<View,String>(splash_LBL_title, "logo_text");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash_screen.this, pair);
                    startActivity(intent);
                }

                finish();
            }
        }, SPLASH_SCREEN);
    }

    private void checkIfGPSOn() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if user GPS is OFF
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertMessageNoGPS();
        } else {
            fetchLocation();
        }
    }

    // Show alert message if user GPS is OFF
    private void alertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your GPS seems to be disable, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    // Has the user given permissions
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void initAnimation() {
        splash_IMG_imageDisplay.startAnimation(splash_top_animation);
        splash_LBL_title.startAnimation(splash_bottom_Animation);
    }

    private void findView() {
        splash_IMG_imageDisplay = findViewById(R.id.splash_IMG_imageDisplay);
        splash_LBL_title = findViewById(R.id.splash_LBL_title);
    }
}