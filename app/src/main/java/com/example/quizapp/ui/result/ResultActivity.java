package com.example.quizapp.ui.result;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.ui.review.ReviewActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreValue = findViewById(R.id.scoreValue);
        TextView performanceText = findViewById(R.id.performanceText);
        TextView correctCount = findViewById(R.id.correctCount);
        TextView incorrectCount = findViewById(R.id.incorrectCount);
        Button reviewBtn = findViewById(R.id.reviewBtn);
        Button finishBtn = findViewById(R.id.finishBtn);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 5);
        int incorrect = total - score;
        ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");

        scoreValue.setText(String.format("%d/%d", score, total));
        correctCount.setText(String.valueOf(score));
        incorrectCount.setText(String.valueOf(incorrect));

        // Performance feedback
        double percentage = ((double) score / total) * 100;
        if (percentage >= 80) {
            performanceText.setText("Excellent!");
            performanceText.setTextColor(android.graphics.Color.GREEN);
        } else if (percentage >= 50) {
            performanceText.setText("Good");
            performanceText.setTextColor(android.graphics.Color.BLUE);
        } else {
            performanceText.setText("Needs Improvement");
            performanceText.setTextColor(android.graphics.Color.RED);
        }

        reviewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("QUESTIONS", questions);
            startActivity(intent);
        });

        Button retryWrongBtn = findViewById(R.id.retryWrongBtn);
        ArrayList<Question> wrongQuestions = new ArrayList<>();
        if (questions != null) {
            for (Question q : questions) {
                if (q.getUserSelectedAnswerIndex() != q.getCorrectAnswerIndex()) {
                    wrongQuestions.add(q);
                }
            }
        }

        if (wrongQuestions.isEmpty()) {
            retryWrongBtn.setVisibility(android.view.View.GONE);
        } else {
            retryWrongBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.quizapp.ui.quiz.QuizActivity.class);
                intent.putExtra("CATEGORY", getIntent().getStringExtra("TOPIC"));
                intent.putExtra("QUESTIONS", wrongQuestions);
                intent.putExtra("RETRY_MODE", true);
                startActivity(intent);
                finish();
            });
        }

        finishBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
