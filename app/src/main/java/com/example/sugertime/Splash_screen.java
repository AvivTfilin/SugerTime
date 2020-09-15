package com.example.sugertime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    private final int SPLASH_SCREEN = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splash_top_animation = AnimationUtils.loadAnimation(this, R.anim.splash_top_animation);
        splash_bottom_Animation = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_animation);

        findView();
        initAnimation();

        handlerSplashScreen();
    }

    private void handlerSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_screen.this, logIn_screen.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
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