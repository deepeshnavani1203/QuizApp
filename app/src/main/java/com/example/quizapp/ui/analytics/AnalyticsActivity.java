package com.example.quizapp.ui.analytics;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.models.QuizResult;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView averageScoreText, totalTimeText, accuracyText;
    private ProgressBar accuracyBar;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        averageScoreText = findViewById(R.id.averageScoreText);
        totalTimeText = findViewById(R.id.totalTimeText);
        accuracyText = findViewById(R.id.accuracyText);
        accuracyBar = findViewById(R.id.accuracyBar);
        backBtn = findViewById(R.id.backBtn);

        loadAnalytics();

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadAnalytics() {
        new Thread(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            List<QuizResult> results = dbHelper.getAllResults();

            if (results == null || results.isEmpty()) {
                runOnUiThread(() -> {
                    averageScoreText.setText("N/A");
                    totalTimeText.setText("0 mins");
                    accuracyText.setText("0%");
                    accuracyBar.setProgress(0);
                });
                return;
            }

            double totalScorePct = 0;
            int totalTimeSeconds = 0;
            double totalAccuracy = 0;
            int count = results.size();

            for (QuizResult result : results) {
                totalScorePct += ((double) result.getScore() / result.getTotalQuestions()) * 100;
                totalTimeSeconds += result.getTimeTaken();
                totalAccuracy += result.getAccuracy();
            }

            double averageScore = totalScorePct / count;
            int totalTimeMinutes = totalTimeSeconds / 60;
            int avgAccuracy = (int) (totalAccuracy / count);

            // Update UI on main thread
            runOnUiThread(() -> {
                averageScoreText.setText(String.format("%.1f%%", averageScore));
                totalTimeText.setText(String.format("%d mins", totalTimeMinutes));
                accuracyText.setText(avgAccuracy + "%");

                // Animate accuracy bar
                accuracyBar.setMax(100);
                ValueAnimator animator = ValueAnimator.ofInt(0, avgAccuracy);
                animator.setDuration(1200);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(a -> accuracyBar.setProgress((int) a.getAnimatedValue()));
                animator.start();
            });
        }).start();
    }
}
