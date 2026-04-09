package com.example.quizapp.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;

public class SecurityManager {

    /**
     * Blocks screenshots and screen recordings.
     */
    public static void blockScreenshots(Activity activity) {
        activity.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );
    }

    /**
     * Enables immersive fullscreen mode to minimize distractions.
     */
    public static void enableImmersiveMode(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
