package com.example.quizapp.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.QuizResult;
import com.example.quizapp.ui.result.ResultActivity;
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.utils.QuestionBank;
import com.example.quizapp.utils.SecurityHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView timerText, questionText, progressText;
    private RecyclerView optionsRecyclerView;
    private Button nextBtn, prevBtn;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private String category, difficulty;
    private boolean isLearnMode;
    
    private CountDownTimer countDownTimer;
    private boolean isQuizFinished = false;
    private OptionAdapter adapter;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SecurityHelper.preventScreenshots(this);
        setContentView(R.layout.activity_quiz);
        enableFullscreen();

        prefManager = new SharedPreferencesManager(this);
        timerText = findViewById(R.id.timerText);
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        optionsRecyclerView = findViewById(R.id.optionsRecyclerView);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);

        category = getIntent().getStringExtra("CATEGORY");
        difficulty = getIntent().getStringExtra("DIFFICULTY");
        isLearnMode = getIntent().getBooleanExtra("LEARN_MODE", false);
        
        loadQuestions();

        if (isLearnMode) {
            timerText.setVisibility(View.GONE);
            prevBtn.setVisibility(View.VISIBLE);
        } else {
            prevBtn.setVisibility(View.GONE);
            startTimer();
        }

        setupOptionsList();
        displayQuestion();
        
        nextBtn.setOnClickListener(v -> handleNextClick());
        prevBtn.setOnClickListener(v -> handlePrevClick());
        
        SecurityHelper.enableDnd(this, true);
    }

    private void loadQuestions() {
        // Load correctly from the new QuestionBank as requested
        questionList = QuestionBank.getQuestions(category, difficulty, isLearnMode);
        
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "Questions unavailable", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupOptionsList() {
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionList.size()) {
            // Error safety for index display
            Toast.makeText(this, "Question Load Error", Toast.LENGTH_SHORT).show();
            return;
        }

        Question q = questionList.get(currentQuestionIndex);
        questionText.setText(q.getQuestionText());
        
        // FIX: Display question number as (currentIndex + 1)
        progressText.setText(String.format(Locale.getDefault(), "%d/%d", currentQuestionIndex + 1, questionList.size()));

        adapter = new OptionAdapter(q, index -> {
            q.setUserSelectedAnswerIndex(index);
            adapter.setSelection(index);
            if (isLearnMode) {
                adapter.showResult(q.getCorrectAnswerIndex());
            }
        });
        
        adapter.setLearnMode(isLearnMode);
        adapter.setSelection(q.getUserSelectedAnswerIndex());
        if (isLearnMode && q.getUserSelectedAnswerIndex() != -1) {
            adapter.showResult(q.getCorrectAnswerIndex());
        }
        
        optionsRecyclerView.setAdapter(adapter);
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long ms) {
                timerText.setText(String.format(Locale.getDefault(), "00:%02d", ms / 1000));
            }
            @Override
            public void onFinish() {
                handleNextClick();
            }
        }.start();
    }

    private void handleNextClick() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
            if (!isLearnMode) startTimer();
        } else {
            finishQuiz();
        }
    }

    private void handlePrevClick() {
        if (isLearnMode && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    private void finishQuiz() {
        if (isQuizFinished) return;
        isQuizFinished = true;
        
        if (countDownTimer != null) countDownTimer.cancel();
        SecurityHelper.enableDnd(this, false);

        if (isLearnMode) {
            Toast.makeText(this, "Mastery Session Complete!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        int score = 0;
        for (Question q : questionList) {
            if (q.getUserSelectedAnswerIndex() == q.getCorrectAnswerIndex()) score++;
        }

        prefManager.addStats(score);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        QuizResult result = new QuizResult(category + " (Test)", score, questionList.size(), date);
        new DatabaseHelper(this).addResult(result);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("TOPIC", category);
        intent.putExtra("QUESTIONS", (ArrayList<Question>) questionList);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isLearnMode) {
            new AlertDialog.Builder(this)
                .setTitle("Exit Learning Mode?")
                .setMessage("Are you sure you want to stop exploring this topic?")
                .setPositiveButton("YES", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("NO", null)
                .show();
        } else {
            new AlertDialog.Builder(this)
                .setTitle("Submit Quiz?")
                .setMessage("Your progress will be submitted. exit?")
                .setPositiveButton("YES", (dialog, which) -> finishQuiz())
                .setNegativeButton("NO", null)
                .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isQuizFinished && !isLearnMode) finishQuiz();
    }

    private void enableFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
