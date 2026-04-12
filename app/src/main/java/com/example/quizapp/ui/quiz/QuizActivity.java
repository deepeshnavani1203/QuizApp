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
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.QuizResult;
import com.example.quizapp.ui.result.ResultActivity;
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.utils.QuizData;
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
    private int score = 0;
    private String category, difficulty;
    private boolean isLearnMode, isRetryMode;
    
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000;
    private boolean isQuizFinished = false;
    private int selectedOptionIndex = -1;
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
        
        ArrayList<Question> passedQuestions = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");
        if (passedQuestions != null) {
            isRetryMode = true;
            questionList = passedQuestions;
            for (Question q : questionList) q.setUserSelectedAnswerIndex(-1);
        } else {
            isRetryMode = false;
            loadQuestions();
        }

        if (isLearnMode) {
            prevBtn.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.INVISIBLE); // Learn mode usually doesn't have timer
        } else {
            Toast.makeText(this, "Test Mode: You cannot go back to previous questions!", Toast.LENGTH_LONG).show();
        }

        setupOptionsList();
        displayQuestion();
        
        nextBtn.setOnClickListener(v -> handleNextClick());
        prevBtn.setOnClickListener(v -> handlePrevClick());
        SecurityHelper.enableDnd(this, true);
    }

    private void loadQuestions() {
        questionList = QuizData.getQuestions(category, difficulty);
        // Test mode is exactly 20. Learn mode can be much more.
        if (!isLearnMode && questionList.size() > 20) {
            questionList = new ArrayList<>(questionList.subList(0, 20));
        }
        
        if (questionList.isEmpty()) {
            Toast.makeText(this, "Questions unavailable", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupOptionsList() {
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question q = questionList.get(currentQuestionIndex);
            questionText.setText(q.getQuestionText());
            
            // Restore selection if exists (important for Learn mode Prev button)
            selectedOptionIndex = q.getUserSelectedAnswerIndex();
            
            adapter = new OptionAdapter(q.getOptions(), index -> {
                selectedOptionIndex = index;
                q.setUserSelectedAnswerIndex(index);
                adapter.setSelection(index);
                
                if (isLearnMode) {
                    adapter.showResult(q.getCorrectAnswerIndex());
                    nextBtn.setText(currentQuestionIndex == questionList.size() - 1 ? "Finish" : "Continue");
                }
            });
            optionsRecyclerView.setAdapter(adapter);
            
            // Re-apply selection in UI if restoring
            if (selectedOptionIndex != -1) {
                adapter.setSelection(selectedOptionIndex);
                if (isLearnMode) adapter.showResult(q.getCorrectAnswerIndex());
            }

            progressText.setText(String.format(Locale.getDefault(), "%d/%d", currentQuestionIndex + 1, questionList.size()));
            if (!isLearnMode) startTimer();
        } else {
            finishQuiz();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timeLeftInMillis = 30000;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long ms) {
                timeLeftInMillis = ms;
                timerText.setText(String.format(Locale.getDefault(), "00:%02d", ms / 1000));
            }
            @Override
            public void onFinish() {
                moveToNext();
            }
        }.start();
    }

    private void handleNextClick() {
        Question q = questionList.get(currentQuestionIndex);
        if (selectedOptionIndex == -1 && !isLearnMode) {
            Toast.makeText(this, "Select an option first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLearnMode && nextBtn.getText().equals("Continue")) {
            nextBtn.setText("Next Question");
            moveToNext();
        } else if (isLearnMode && nextBtn.getText().equals("Finish")) {
            finishQuiz();
        } else if (!isLearnMode) {
            moveToNext();
        }
    }

    private void handlePrevClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    private void moveToNext() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        if (isQuizFinished) return;
        isQuizFinished = true;
        
        if (countDownTimer != null) countDownTimer.cancel();
        SecurityHelper.enableDnd(this, false);

        score = 0;
        for (Question q : questionList) {
            if (q.getUserSelectedAnswerIndex() == q.getCorrectAnswerIndex()) score++;
        }

        // Cumulative Stats Update
        if (!isRetryMode) {
            prefManager.addStats(score);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            String modeStr = isLearnMode ? "Learn" : "Test";
            QuizResult result = new QuizResult(category + " (" + modeStr + ")", score, questionList.size(), date);
            new DatabaseHelper(this).addResult(result);
        }

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
        new AlertDialog.Builder(this)
                .setTitle("Submit Quiz?")
                .setMessage("Your progress will be submitted. Do you want to exit?")
                .setPositiveButton("YES", (dialog, which) -> finishQuiz())
                .setNegativeButton("NO", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isQuizFinished) finishQuiz();
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
