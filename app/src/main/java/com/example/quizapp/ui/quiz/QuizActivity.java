package com.example.quizapp.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView timerText, questionText, progressText;
    private RadioGroup optionsGroup;
    private RadioButton[] options = new RadioButton[4];
    private Button nextBtn;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String category, difficulty;
    private boolean isLearnMode;
    private CountDownTimer countDownTimer;
    private boolean isQuizFinished = false;
    private long timeLeftInMillis = 1800000; // 30 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SecurityHelper.preventScreenshots(this);
        setContentView(R.layout.activity_quiz);
        enableFullscreen();

        timerText = findViewById(R.id.timerText);
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        optionsGroup = findViewById(R.id.optionsGroup);
        options[0] = findViewById(R.id.option1);
        options[1] = findViewById(R.id.option2);
        options[2] = findViewById(R.id.option3);
        options[3] = findViewById(R.id.option4);
        nextBtn = findViewById(R.id.nextBtn);

        category = getIntent().getStringExtra("CATEGORY");
        isLearnMode = getIntent().getBooleanExtra("LEARN_MODE", false);
        difficulty = getIntent().getStringExtra("DIFFICULTY");

        if (getIntent().hasExtra("QUESTIONS")) {
            questionList = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");
            displayQuestion();
        } else {
            loadQuestions();
        }
        
        startTimer();

        nextBtn.setOnClickListener(v -> processNext());
        
        SecurityHelper.enableDnd(this, true);
    }

    private void loadQuestions() {
        questionList = QuizData.getQuestionsByTopic(category);
        Collections.shuffle(questionList);
        
        // Limit to 20 questions as requested
        if (questionList.size() > 20) {
            questionList = new ArrayList<>(questionList.subList(0, 20));
        }
        
        displayQuestion();
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question q = questionList.get(currentQuestionIndex);
            questionText.setText(q.getQuestionText());
            
            List<String> optList = q.getOptions();
            for (int i = 0; i < 4; i++) {
                options[i].setText(optList.get(i));
                options[i].setEnabled(true);
                options[i].setBackgroundResource(R.drawable.option_background_selector);
            }
            
            optionsGroup.clearCheck();
            progressText.setText(String.format(Locale.getDefault(), "Q %d/%d", currentQuestionIndex + 1, questionList.size()));
        } else {
            finishQuiz();
        }
    }

    private void processNext() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedButton = findViewById(selectedId);
        int answerIndex = optionsGroup.indexOfChild(selectedButton);
        Question currentQuestion = questionList.get(currentQuestionIndex);
        currentQuestion.setUserSelectedAnswerIndex(answerIndex);

        if (isLearnMode) {
            highlightCorrectAnswer();
            nextBtn.setText("Continue");
            nextBtn.setOnClickListener(v -> {
                moveToNext();
                nextBtn.setText("Next Question");
                nextBtn.setOnClickListener(v1 -> processNext());
            });
        } else {
            if (answerIndex == currentQuestion.getCorrectAnswerIndex()) {
                score++;
            }
            moveToNext();
        }
    }

    private void highlightCorrectAnswer() {
        Question q = questionList.get(currentQuestionIndex);
        int correctIndex = q.getCorrectAnswerIndex();
        int selectedIndex = q.getUserSelectedAnswerIndex();

        for (int i = 0; i < 4; i++) {
            options[i].setEnabled(false);
            if (i == correctIndex) {
                options[i].setBackgroundColor(android.graphics.Color.YELLOW);
            }
        }
        
        if (selectedIndex == correctIndex) {
            score++;
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

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                timerText.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                autoSubmit("Time's up!");
            }
        }.start();
    }

    private void finishQuiz() {
        if (isQuizFinished) return;
        isQuizFinished = true;
        
        if (countDownTimer != null) countDownTimer.cancel();
        SecurityHelper.enableDnd(this, false);

        // Save result locally
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        QuizResult result = new QuizResult(category + " (" + difficulty + ")", score, questionList.size(), date);
        new DatabaseHelper(this).addResult(result);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("TOPIC", category);
        intent.putExtra("QUESTIONS", (ArrayList<Question>) questionList);
        startActivity(intent);
        finish();
    }

    private void autoSubmit(String reason) {
        if (!isQuizFinished) {
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
            finishQuiz();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("If you exit, your quiz will be submitted. Continue?")
                .setPositiveButton("YES", (dialog, which) -> finishQuiz())
                .setNegativeButton("NO", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isQuizFinished) {
            autoSubmit("Quiz auto-submitted: App minimized");
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        if (isInMultiWindowMode && !isQuizFinished) {
            autoSubmit("Quiz terminated: Split-screen detected");
        }
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
