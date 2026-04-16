package com.example.quizapp.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.widget.Toast;

public class CalendarHelper {

    /**
     * Opens the system calendar with a pre-filled revision event.
     * Uses Intent.ACTION_INSERT — no calendar permissions needed.
     * Works on all Android versions including 11+ (avoids resolveActivity restriction).
     */
    public static void addPracticeReminder(Context context, String topic,
                                           int score, int total, long startMillis) {
        long endMillis = startMillis + (30 * 60 * 1000); // 30-minute event

        String title = "Quizify – Revise " + topic;
        String description;
        if (total > 0) {
            double pct = (score * 100.0 / total);
            description = String.format(
                "Last attempt: %d/%d (%.0f%%)\nRevision session added by Quizify.\n" +
                "This feature analyzes user confidence based on response time.",
                score, total, pct);
        } else {
            description = "Study session added by Quizify.\n" +
                "This feature analyzes user confidence based on response time.";
        }

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setType("vnd.android.cursor.item/event")
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.ALL_DAY, false)
                .putExtra(CalendarContract.Events.HAS_ALARM, 1);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No calendar app found on this device.", Toast.LENGTH_SHORT).show();
        }
    }
}
