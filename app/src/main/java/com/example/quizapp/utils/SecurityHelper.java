package com.example.quizapp.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

public class SecurityHelper {

    private static final String PREF_DND_ASKED = "dnd_permission_asked";

    public static void preventScreenshots(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static boolean hasDndPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return nm != null && nm.isNotificationPolicyAccessGranted();
        }
        return true;
    }

    public static void requestDndPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Mark that we've asked so we don't ask again
            activity.getSharedPreferences("QuizifyPrefs", Context.MODE_PRIVATE)
                    .edit().putBoolean(PREF_DND_ASKED, true).apply();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            activity.startActivity(intent);
        }
    }

    /** Returns true if we have already asked the user for DND permission before */
    public static boolean hasDndBeenAsked(Context context) {
        return context.getSharedPreferences("QuizifyPrefs", Context.MODE_PRIVATE)
                .getBoolean(PREF_DND_ASKED, false);
    }

    public static void enableDnd(Context context, boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null && nm.isNotificationPolicyAccessGranted()) {
                nm.setInterruptionFilter(enable
                        ? NotificationManager.INTERRUPTION_FILTER_NONE
                        : NotificationManager.INTERRUPTION_FILTER_ALL);
            }
            // If permission not granted, silently skip — no dialog during quiz
        }
    }
}
