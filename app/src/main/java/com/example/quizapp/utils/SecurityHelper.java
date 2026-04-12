package com.example.quizapp.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

public class SecurityHelper {

    public static void preventScreenshots(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static boolean hasDndPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.isNotificationPolicyAccessGranted();
        }
        return true;
    }

    public static void requestDndPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            activity.startActivity(intent);
        }
    }

    public static void enableDnd(Context context, boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
                if (enable) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                } else {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
            }
        }
    }
}
