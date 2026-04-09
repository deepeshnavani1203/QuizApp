package com.example.quizapp.ui.quiz;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.R;

public class ResultActivity extends AppCompatActivity {

    private TextView scoreText, analysisText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreText = findViewById(R.id.scoreText);
        analysisText = findViewById(R.id.analysisText);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        String weakTopics = getIntent().getStringExtra("weakTopics");

        scoreText.setText(String.format("You scored %d/%d", score, total));
        analysisText.setText("AI Feedback: Your weak topics are " + (weakTopics != null ? weakTopics : "None yet. Keep it up!"));
    }
}
