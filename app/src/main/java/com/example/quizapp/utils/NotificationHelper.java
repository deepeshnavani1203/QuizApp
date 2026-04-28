package com.example.quizapp.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.quizapp.R;
import com.example.quizapp.ui.auth.SplashActivity;

public class NotificationHelper {

    public static final String CHANNEL_ID = "quizify_channel";
    public static final String CHANNEL_NAME = "Quizify Reminders";
    private static final int DAILY_REMINDER_ID = 1001;
    private static final int RESULT_NOTIF_ID = 1002;

    /** Call once at app start (e.g. MainActivity) to register the channel */
    public static void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Quiz reminders and result updates");
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
    }

    /** Show an immediate notification after quiz result */
    public static void showResultNotification(Context context, String topic, int score, int total, int confidencePct, String timeTaken) {
        String title = "Quiz Complete – " + topic;
        String body = "Score: " + score + "/" + total +
                " · Time: " + timeTaken +
                " · Accuracy: " + confidencePct + "%" +
                (confidencePct >= 75 ? " 💪" : confidencePct >= 40 ? " 🤔" : " 😅");

        Intent tapIntent = new Intent(context, SplashActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, RESULT_NOTIF_ID, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_quiz)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        try {
            nm.notify(RESULT_NOTIF_ID, builder.build());
        } catch (SecurityException e) {
            // POST_NOTIFICATIONS permission not granted on Android 13+
        }
    }

    /** Schedule a daily practice reminder at the given hour (24h) */
    public static void scheduleDailyReminder(Context context, int hourOfDay) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, DAILY_REMINDER_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        // If the time has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pi
            );
        }
    }

    /** Cancel the daily reminder */
    public static void cancelDailyReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, DAILY_REMINDER_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) alarmManager.cancel(pi);
    }

    /** BroadcastReceiver that fires the daily reminder notification */
    public static class ReminderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = "Time to practice! 🧠";
            String body = "Keep your streak alive — take a quick quiz on Quizify today!";

            Intent tapIntent = new Intent(context, SplashActivity.class);
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi = PendingIntent.getActivity(context, 0, tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_quiz)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pi)
                    .setAutoCancel(true);

            NotificationManagerCompat nm = NotificationManagerCompat.from(context);
            try {
                nm.notify(DAILY_REMINDER_ID, builder.build());
            } catch (SecurityException e) {
                // Permission not granted
            }
        }
    }
}
