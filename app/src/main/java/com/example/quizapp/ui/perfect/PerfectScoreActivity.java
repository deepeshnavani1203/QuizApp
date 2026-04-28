package com.example.quizapp.ui.perfect;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;

public class PerfectScoreActivity extends AppCompatActivity {

    private ImageView celebrationIcon;
    private TextView congratsText, scoreText;
    private Button homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_score);

        celebrationIcon = findViewById(R.id.celebrationIcon);
        congratsText = findViewById(R.id.congratsText);
        scoreText = findViewById(R.id.scoreText);
        homeBtn = findViewById(R.id.homeBtn);

        String topic = getIntent().getStringExtra("TOPIC");
        int totalQuestions = getIntent().getIntExtra("TOTAL", 20);

        scoreText.setText("You scored 20/20!");
        congratsText.setText("🎉 Congratulations! 🎉\nYou got all answers correct!");

        // Add animation to the celebration icon
        animateCelebration();

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void animateCelebration() {
        // Scale animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(celebrationIcon, "scaleX", 0.5f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(celebrationIcon, "scaleY", 0.5f, 1.2f, 1.0f);

        // Rotation animation
        ObjectAnimator rotation = ObjectAnimator.ofFloat(celebrationIcon, "rotation", 0f, 360f);

        // Combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotation);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }
}
