package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.Theme_FurEver_Splash);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            
            // Short delay before checking auth and launching appropriate activity
            new Handler().postDelayed(() -> {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                Intent intent;
                
                if (currentUser != null) {
                    // User is signed in
                    Log.d(TAG, "User is signed in: " + currentUser.getEmail());
                    intent = new Intent(this, MainActivity.class);
                } else {
                    // No user is signed in
                    Log.d(TAG, "No user is signed in");
                    intent = new Intent(this, LoginActivity.class);
                }
                
                startActivity(intent);
                finish();
            }, 1500); // 1.5 seconds
        } catch (Exception e) {
            Log.e(TAG, "Error in SplashActivity: ", e);
        }
    }
}
