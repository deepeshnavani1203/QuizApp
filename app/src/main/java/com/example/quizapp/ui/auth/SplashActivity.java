package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferencesManager prefManager = new SharedPreferencesManager(this);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
            prefManager.isDarkMode() ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES 
                                     : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1500);
        logo.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            // ONE-TIME CLEANUP LOGIC
            android.content.SharedPreferences prefs = getSharedPreferences("CleanupPrefs", MODE_PRIVATE);
            if (!prefs.getBoolean("is_cleaned_v1", false)) {
                prefManager.clearData();
                new com.example.quizapp.utils.DatabaseHelper(this).clearHistory();
                prefs.edit().putBoolean("is_cleaned_v1", true).apply();
            }

            Intent intent;
            if (prefManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2500);
    }
}
