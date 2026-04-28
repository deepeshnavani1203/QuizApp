package com.example.quizapp.ui.result;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.ui.perfect.PerfectScoreActivity;
import com.example.quizapp.ui.review.ReviewActivity;
import com.example.quizapp.utils.CalendarHelper;
import com.example.quizapp.utils.NotificationHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private LottieAnimationView resultAnimation;
    private int finalScore, finalTotal, finalConfidencePct;
    private String finalTopic;

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    private final ActivityResultLauncher<String> notifPermLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    postResultNotification();
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
        finalTopic = getIntent().getStringExtra("TOPIC");
        String difficulty = getIntent().getStringExtra("DIFFICULTY");
        ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");

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

        // --- Per-Question Analysis ---
        if (questions != null) {
            RecyclerView analysisRecyclerView = findViewById(R.id.analysisRecyclerView);
            analysisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            analysisRecyclerView.setAdapter(new AnalysisAdapter(questions));
            
            showRecommendations(questions);
        }

        // --- Export Feature ---
        findViewById(R.id.exportBtn).setOnClickListener(v -> exportResults(questions));
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }

    private void showRecommendations(List<Question> questions) {
        Map<String, Integer> wrongCounts = new HashMap<>();
        for (Question q : questions) {
            if (q.getUserSelectedAnswerIndex() != q.getCorrectAnswerIndex()) {
                String topic = q.getTopic();
                wrongCounts.put(topic, wrongCounts.getOrDefault(topic, 0) + 1);
            }
        }

        if (!wrongCounts.isEmpty()) {
            StringBuilder sb = new StringBuilder("Based on your mistakes, you should focus on:\n");
            for (String topic : wrongCounts.keySet()) {
                sb.append("• ").append(topic).append("\n");
            }
            findViewById(R.id.recommendationCard).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.weakTopicsText)).setText(sb.toString().trim());
        }
    }

    private void exportResults(List<Question> questions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Quiz Result: ").append(finalTopic).append("\n");
        sb.append("Score: ").append(finalScore).append("/").append(finalTotal).append("\n");
        sb.append("Accuracy: ").append(finalConfidencePct).append("%\n\n");
        sb.append("--- Questions ---\n");
        
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            sb.append(i + 1).append(". ").append(q.getQuestionText()).append("\n");
            sb.append("Your Answer: ").append(q.getUserSelectedAnswerIndex() != -1 ? q.getOptions().get(q.getUserSelectedAnswerIndex()) : "Skipped").append("\n");
            sb.append("Correct Answer: ").append(q.getCorrectAnswer()).append("\n\n");
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Result - " + finalTopic);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(intent, "Export Quiz Result"));
    }

    private void postResultNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        NotificationHelper.showResultNotification(this, finalTopic,
                finalScore, finalTotal, finalConfidencePct, formatTime(getIntent().getIntExtra("TIME_TAKEN", 0)));
    }

    // --- Inner Analysis Adapter ---
    private class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {
        private List<Question> questions;

        AnalysisAdapter(List<Question> questions) { this.questions = questions; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_analysis, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Question q = questions.get(position);
            holder.number.setText(String.valueOf(position + 1));
            
            boolean isCorrect = q.getUserSelectedAnswerIndex() == q.getCorrectAnswerIndex();
            holder.status.setText(isCorrect ? "Correct" : (q.getUserSelectedAnswerIndex() == -1 ? "Skipped" : "Incorrect"));
            holder.status.setTextColor(isCorrect ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#F44336"));
            holder.time.setText("Time: " + (q.getTimeTakenMs() / 1000) + "s");
            holder.icon.setImageResource(isCorrect ? R.drawable.ic_check : R.drawable.ic_close);
            holder.icon.setColorFilter(isCorrect ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#F44336"));
            
            android.graphics.drawable.GradientDrawable bg = (android.graphics.drawable.GradientDrawable) holder.number.getBackground();
            bg.setColor(isCorrect ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#F44336"));
        }

        @Override
        public int getItemCount() { return questions.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView number, status, time;
            ImageView icon;
            ViewHolder(View v) {
                super(v);
                number = v.findViewById(R.id.qNumber);
                status = v.findViewById(R.id.qStatus);
                time = v.findViewById(R.id.qTime);
                icon = v.findViewById(R.id.statusIcon);
            }
        }
    }
}
