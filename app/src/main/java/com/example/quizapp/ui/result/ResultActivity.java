package com.example.quizapp.ui.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.ui.review.ReviewActivity;
import com.example.quizapp.ui.quiz.QuizActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private LottieAnimationView resultAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultAnimation = findViewById(R.id.resultAnimation);
        TextView scoreValue = findViewById(R.id.scoreValue);
        TextView performanceText = findViewById(R.id.performanceText);
        TextView correctCount = findViewById(R.id.correctCount);
        TextView incorrectCount = findViewById(R.id.incorrectCount);
        Button reviewBtn = findViewById(R.id.reviewBtn);
        Button finishBtn = findViewById(R.id.finishBtn);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 1);
        int incorrect = total - score;
        ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");

        scoreValue.setText(String.format("%d/%d", score, total));
        correctCount.setText(String.valueOf(score));
        incorrectCount.setText(String.valueOf(incorrect));

        double percentage = ((double) score / total) * 100;
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

        reviewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("QUESTIONS", questions);
            startActivity(intent);
        });

        finishBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
