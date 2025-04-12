package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_FurEver_Splash);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Short delay before launching login/register
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1500); // 1.5 seconds
    }
}
