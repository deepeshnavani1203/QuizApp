package com.example.quizapp.ui.result;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.ui.perfect.PerfectScoreActivity;
import com.example.quizapp.ui.review.ReviewActivity;
import com.example.quizapp.utils.CalendarHelper;
import com.example.quizapp.utils.NotificationHelper;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private LottieAnimationView resultAnimation;
    private int finalScore, finalTotal, finalConfidencePct;
    private String finalTopic;

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    private final ActivityResultLauncher<String> notifPermLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    NotificationHelper.showResultNotification(this, finalTopic,
                            finalScore, finalTotal, finalConfidencePct);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultAnimation = findViewById(R.id.resultAnimation);
        TextView scoreValue = findViewById(R.id.scoreValue);
        TextView performanceText = findViewById(R.id.performanceText);
        TextView correctCount = findViewById(R.id.correctCount);
        TextView incorrectCount = findViewById(R.id.incorrectCount);
        TextView confidenceText = findViewById(R.id.confidenceText);
        TextView confidenceLabel = findViewById(R.id.confidenceLabel);
        ProgressBar confidenceBar = findViewById(R.id.confidenceBar);
        Button reviewBtn = findViewById(R.id.reviewBtn);
        Button calendarBtn = findViewById(R.id.calendarBtn);
        Button finishBtn = findViewById(R.id.finishBtn);

        finalScore = getIntent().getIntExtra("SCORE", 0);
        finalScore = Math.max(0, finalScore); // Prevent negative scores
        finalTotal = getIntent().getIntExtra("TOTAL", 1);
        int finalCorrect = getIntent().getIntExtra("CORRECT", finalScore);
        int finalIncorrect = getIntent().getIntExtra("INCORRECT", finalTotal - finalCorrect);
        int timeTaken = getIntent().getIntExtra("TIME_TAKEN", 0);
        finalTopic = getIntent().getStringExtra("TOPIC");
        String difficulty = getIntent().getStringExtra("DIFFICULTY");

        // Check for perfect score (20/20)
        if (finalCorrect == finalTotal && finalTotal == 20) {
            Intent perfectIntent = new Intent(this, PerfectScoreActivity.class);
            perfectIntent.putExtra("TOPIC", finalTopic);
            perfectIntent.putExtra("TOTAL", finalTotal);
            startActivity(perfectIntent);
            finish();
            return;
        }

        scoreValue.setText(String.format("%d/%d", finalScore, finalTotal));
        correctCount.setText(String.valueOf(finalCorrect));
        incorrectCount.setText(String.valueOf(finalIncorrect));

        double percentage = ((double) finalScore / finalTotal) * 100;
        if (percentage >= 80) {
            performanceText.setText("Congratulations! You're a Master!");
            performanceText.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else if (percentage >= 50) {
            performanceText.setText("Good Job! Keep practicing!");
            performanceText.setTextColor(android.graphics.Color.parseColor("#2196F3"));
        } else {
            performanceText.setText("Practice more to improve!");
            performanceText.setTextColor(android.graphics.Color.parseColor("#F44336"));
        }

        // --- Confidence Meter (Accuracy-based) ---
        finalConfidencePct = (int) ((finalCorrect * 100.0) / finalTotal);

        confidenceBar.setMax(100);
        ValueAnimator animator = ValueAnimator.ofInt(0, finalConfidencePct);
        animator.setDuration(1200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> confidenceBar.setProgress((int) a.getAnimatedValue()));
        animator.start();

        confidenceText.setText(finalConfidencePct + "%");

        String label;
        int labelColor;
        if (finalConfidencePct >= 75) {
            label = "Accuracy: " + finalConfidencePct + "% - Excellent! 💪";
            labelColor = android.graphics.Color.parseColor("#4CAF50");
        } else if (finalConfidencePct >= 50) {
            label = "Accuracy: " + finalConfidencePct + "% - Good effort! 🤔";
            labelColor = android.graphics.Color.parseColor("#FF9800");
        } else {
            label = "Accuracy: " + finalConfidencePct + "% - Keep improving! 😅";
            labelColor = android.graphics.Color.parseColor("#F44336");
        }
        confidenceLabel.setText(label);
        confidenceLabel.setTextColor(labelColor);

        // --- Fire result notification ---
        postResultNotification();

        // --- Calendar button ---
        calendarBtn.setOnClickListener(v -> {
            // Schedule revision for tomorrow at 10 AM
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 10);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            CalendarHelper.addPracticeReminder(this, finalTopic,
                    finalScore, finalTotal, cal.getTimeInMillis());
        });

        reviewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("QUESTIONS", questions);
            startActivity(intent);
        });

        Button analyticsBtn = findViewById(R.id.analyticsBtn);
        analyticsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.quizapp.ui.analytics.AnalyticsActivity.class);
            startActivity(intent);
        });

        finishBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void postResultNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        NotificationHelper.showResultNotification(this, finalTopic,
                finalScore, finalTotal, finalConfidencePct);
    }
}
