package com.example.android.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

/**
 * Created by Noha Farid on 3/30/2018.
 */

public class SplashActivity extends AppCompatActivity {
    // Time for the splash
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Initialize a new GradientDrawable
        GradientDrawable gd = new GradientDrawable();

        // Set the color array to draw gradient
        gd.setColors(new int[]{
                Color.parseColor("#388E3C"),
                Color.parseColor("#43A047"),
                Color.parseColor("#4CAF50"),

        });

        // Set the GradientDrawable gradient type linear gradient
        gd.setGradientType(GradientDrawable.SWEEP_GRADIENT);

        RelativeLayout splashBackground = findViewById(R.id.splash_background);
        splashBackground.setBackground(gd);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
