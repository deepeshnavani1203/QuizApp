package com.example.quizapp.ui.quiz;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.SecurityManager;

public class QuizActivity extends AppCompatActivity {

    private TextView timerText;
    private CountDownTimer countDownTimer;
    private boolean isQuizSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Enforce Security: Block Screenshots
        SecurityManager.blockScreenshots(this);
        
        setContentView(R.layout.activity_quiz);

        // 2. Enable Immersive Mode
        SecurityManager.enableImmersiveMode(this);

        timerText = findViewById(R.id.timerText);
        startTimer(600000); // 10 minutes default
    }

    private void startTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds %= 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                submitQuiz("Time Up!");
            }
        }.start();
    }

    private void submitQuiz(String reason) {
        if (isQuizSubmitted) return;
        isQuizSubmitted = true;
        if (countDownTimer != null) countDownTimer.cancel();
        
        Toast.makeText(this, "Quiz Submitted: " + reason, Toast.LENGTH_LONG).show();
        // Logic to POST results to backend would go here
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 3. Anti-Cheating: Detect App Exit/Minimize
        if (!isQuizSubmitted) {
            submitQuiz("App Minimized or Switched (Anti-Cheating Trigger)");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Ensure immersive mode persists
        if (hasFocus) {
            SecurityManager.enableImmersiveMode(this);
        } else {
            // 4. Track focus loss (e.g., notification panel pulled down)
            if (!isQuizSubmitted) {
                Toast.makeText(this, "Focus Lost! Suspicious activity logged.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
