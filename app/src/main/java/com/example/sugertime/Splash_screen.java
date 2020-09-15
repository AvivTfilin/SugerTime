package com.example.sugertime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_screen extends AppCompatActivity {

    ImageView splash_IMG_imageDisplay;
    TextView splash_LBL_title;

    Animation splash_top_animation;
    Animation splash_bottom_Animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splash_top_animation = AnimationUtils.loadAnimation(this, R.anim.splash_top_animation);
        splash_bottom_Animation = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_animation);

        findView();
        initAnimation();
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