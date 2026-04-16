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
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.utils.QuestionBank;
import com.example.quizapp.utils.SecurityHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuizActivity extends AppCompatActivity {

    // Removed hardcoded URL, now directly uses BackendService.BASE_URL
    private TextView timerText, questionText, progressText, scoreText, categoryTitleTop;
    private RecyclerView optionsRecyclerView;
    private Button nextBtn, prevBtn;
    private View skeletonLayout;
    private androidx.constraintlayout.widget.Group quizContentGroup;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private String category, difficulty;
    private boolean isLearnMode;
    private String currentQuizId;
    private long quizStartTime;

    private CountDownTimer countDownTimer;
    private boolean isQuizFinished = false;
    private int sessionScore = 0;
    private OptionAdapter adapter;
    private SharedPreferencesManager prefManager;
    private long questionStartTime; // tracks when current question was displayed

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
        scoreText = findViewById(R.id.scoreText);
        categoryTitleTop = findViewById(R.id.categoryTitleTop);
        optionsRecyclerView = findViewById(R.id.optionsRecyclerView);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        skeletonLayout = findViewById(R.id.skeletonLayout);
        quizContentGroup = findViewById(R.id.quizContentGroup);

        category = getIntent().getStringExtra("CATEGORY");
        difficulty = getIntent().getStringExtra("DIFFICULTY");
        isLearnMode = getIntent().getBooleanExtra("LEARN_MODE", false);
        quizStartTime = System.currentTimeMillis();

        loadQuestions();

        if (isLearnMode) {
            timerText.setVisibility(View.GONE);
            prevBtn.setVisibility(View.VISIBLE);
        } else {
            prevBtn.setVisibility(View.GONE);
        }

        setupOptionsList();

        nextBtn.setOnClickListener(v -> handleNextClick(false));
        prevBtn.setOnClickListener(v -> handlePrevClick());

        // DND is enabled after questions load, inside loadQuestions() callback
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();
        new Thread(() -> {
            List<Question> fetched = fetchQuestionsFromBackend(category, difficulty, isLearnMode);
            runOnUiThread(() -> {
                skeletonLayout.setVisibility(View.GONE);
                quizContentGroup.setVisibility(View.VISIBLE);

                if (fetched != null && !fetched.isEmpty()) {
                    questionList = fetched;
                    currentQuestionIndex = 0;
                    displayQuestion();
                    if (!isLearnMode) {
                        scoreText.setVisibility(View.VISIBLE);
                        startTimer();
                        // Silently enable DND if permission was already granted
                        SecurityHelper.enableDnd(this, true);
                    } else {
                        scoreText.setVisibility(View.GONE);
                    }
                } else {
                    useFallbackQuestions();
                }
            });
        }).start();
    }

    private void useFallbackQuestions() {
        String localCategory = mapCategoryToLocal(category);
        questionList = QuestionBank.getQuestions(localCategory, difficulty, isLearnMode);
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "Questions unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Toast.makeText(this, "Using offline questions", Toast.LENGTH_SHORT).show();
        currentQuestionIndex = 0;
        displayQuestion();
        if (!isLearnMode) {
            startTimer();
            SecurityHelper.enableDnd(this, true);
        }
    }

    private String mapCategoryToLocal(String category) {
        switch (category) {
            case "Java Basics":
                return "Java";
            case "C Programming":
                return "C";
            case "C++ Basics":
                return "C++";
            case "Python Basics":
                return "Python";
            case "JavaScript Basics":
                return "JS";
            case "Git Fundamentals":
                return "Git";
            case "Operating Systems":
                return "OS";
            case "ReactJS":
                return "React";
            case "Node.js":
                return "Node.js";
            case "DBMS":
                return "DBMS";
            default:
                return category;
        }
    }

    private String mapLocalCategoryToBackend(String category) {
        switch (category) {
            case "Java": return "Java Basics";
            case "C": return "C Programming";
            case "C++": return "C++ Basics";
            case "Python": return "Python Basics";
            case "JS": return "JavaScript Basics";
            case "Git": return "Git Fundamentals";
            case "OS": return "Operating Systems";
            case "React": return "ReactJS";
            case "Node.js": return "Node.js";
            case "DBMS": return "DBMS";
            default: return category;
        }
    }

    private List<Question> fetchQuestionsFromBackend(String category, String difficulty, boolean isLearnMode) {
        try {
            String quizzesResponse = getJsonFromUrl(BackendService.BASE_URL + "/api/quizzes");
            if (quizzesResponse == null)
                return null;

            JSONArray quizzes = new JSONArray(quizzesResponse);
            String quizId = null;
            String backendCategory = mapLocalCategoryToBackend(category);
            for (int i = 0; i < quizzes.length(); i++) {
                JSONObject quiz = quizzes.getJSONObject(i);
                if (backendCategory.equalsIgnoreCase(quiz.optString("title"))) {
                    quizId = quiz.optString("_id");
                    break;
                }
            }
            if (quizId == null && quizzes.length() > 0) {
                quizId = quizzes.getJSONObject(0).optString("_id");
            }
            if (quizId == null)
                return null;
            currentQuizId = quizId;

            String quizResponse = getJsonFromUrl(BackendService.BASE_URL + "/api/quizzes/" + quizId);
            if (quizResponse == null)
                return null;

            JSONObject quizObject = new JSONObject(quizResponse);
            JSONArray questionsJson = quizObject.optJSONArray("questions");
            if (questionsJson == null)
                return null;

            List<Question> questions = new ArrayList<>();
            java.util.Set<String> seenTexts = new java.util.HashSet<>();
            for (int i = 0; i < questionsJson.length(); i++) {
                JSONObject q = questionsJson.getJSONObject(i);
                String questionText = q.optString("questionText");
                if (questionText != null && !questionText.trim().isEmpty()) {
                    if (seenTexts.contains(questionText.trim().toLowerCase())) continue;
                    seenTexts.add(questionText.trim().toLowerCase());
                }
                JSONArray optionsJson = q.optJSONArray("options");
                List<String> options = new ArrayList<>();
                for (int j = 0; optionsJson != null && j < optionsJson.length(); j++) {
                    options.add(optionsJson.optString(j));
                }
                int correctOptionIndex = q.optInt("correctOptionIndex", 0);
                String topic = q.optString("topic");
                String diff = q.optString("difficulty", "Medium");
                questions.add(new Question(questionText, options, correctOptionIndex, topic, diff));
            }

            if (!isLearnMode && !"Random".equalsIgnoreCase(difficulty)) {
                List<Question> filtered = new ArrayList<>();
                for (Question q : questions) {
                    if (difficulty.equalsIgnoreCase(q.getDifficulty())) {
                        filtered.add(q);
                    }
                }
                Collections.shuffle(filtered);
                return filtered.size() > 20 ? new ArrayList<>(filtered.subList(0, 20)) : filtered;
            }

            Collections.shuffle(questions);
            if (isLearnMode) {
                return questions.size() > 150 ? new ArrayList<>(questions.subList(0, 150)) : questions;
            } else {
                return questions.size() > 20 ? new ArrayList<>(questions.subList(0, 20)) : questions;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getJsonFromUrl(String urlString) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
                return null;

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            if (reader != null)
                reader.close();
            if (connection != null)
                connection.disconnect();
        }
    }

    private void submitResultToBackend(int score, int totalQuestions, int correctAnswers, int wrongAnswers,
            int timeTaken, List<Question> questions) {
        String userId = prefManager.getUserId();
        if (userId == null || currentQuizId == null)
            return;

        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("userId", userId);
                payload.put("quizId", currentQuizId);
                payload.put("score", score);
                payload.put("totalQuestions", totalQuestions);
                payload.put("correctAnswers", correctAnswers);
                payload.put("wrongAnswers", wrongAnswers);
                payload.put("timeTaken", timeTaken);
                payload.put("suspiciousActivity", new JSONArray());

                JSONObject topicPerf;
                JSONArray topicPerformance = new JSONArray();
                Map<String, int[]> stats = new HashMap<>();
                for (Question q : questions) {
                    String topic = q.getTopic() != null ? q.getTopic() : "General";
                    int[] counts = stats.getOrDefault(topic, new int[2]);
                    counts[1]++;
                    if (q.getUserSelectedAnswerIndex() == q.getCorrectAnswerIndex()) {
                        counts[0]++;
                    }
                    stats.put(topic, counts);
                }
                for (String topic : stats.keySet()) {
                    int[] counts = stats.get(topic);
                    topicPerf = new JSONObject();
                    topicPerf.put("topic", topic);
                    topicPerf.put("correct", counts[0]);
                    topicPerf.put("total", counts[1]);
                    topicPerformance.put(topicPerf);
                }
                payload.put("topicPerformance", topicPerformance);
                BackendService.submitResult(payload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupOptionsList() {
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionList.size()) {
            Toast.makeText(this, "Question Load Error", Toast.LENGTH_SHORT).show();
            return;
        }

        // Record when this question started being shown
        questionStartTime = System.currentTimeMillis();

        Question q = questionList.get(currentQuestionIndex);
        questionText.setText(q.getQuestionText());

        // FIX: Display question number as (currentIndex + 1)
        progressText
                .setText(String.format(Locale.getDefault(), "%d/%d", currentQuestionIndex + 1, questionList.size()));

        categoryTitleTop.setText(category + " Quiz");

        if (currentQuestionIndex == questionList.size() - 1) {
            nextBtn.setText("Submit Test");
        } else {
            nextBtn.setText("Next Question");
        }

        scoreText.setText("Score: " + sessionScore);

        adapter = new OptionAdapter(q, index -> {
            q.setUserSelectedAnswerIndex(index);
            adapter.setSelection(index);
            if (isLearnMode) {
                adapter.showResult(q.getCorrectAnswerIndex());
            }
        });

        adapter.setLearnMode(isLearnMode);
        adapter.setSelection(q.getUserSelectedAnswerIndex());
        if (isLearnMode) {
            adapter.showResult(q.getCorrectAnswerIndex());
        }

        optionsRecyclerView.setAdapter(adapter);
    }

    private void startTimer() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        
        long duration = 30000; // Default Easy
        if ("Medium".equalsIgnoreCase(difficulty)) duration = 25000;
        else if ("Hard".equalsIgnoreCase(difficulty)) duration = 20000;

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long ms) {
                timerText.setText(String.format(Locale.getDefault(), "00:%02d", ms / 1000));
            }

            @Override
            public void onFinish() {
                handleNextClick(true);
            }
        }.start();
    }

    private void handleNextClick(boolean isAutoSkip) {
        if (!isLearnMode) {
            Question currentQ = questionList.get(currentQuestionIndex);

            // Record time spent on this question
            currentQ.setTimeTakenMs(System.currentTimeMillis() - questionStartTime);

            int selectedIndex = currentQ.getUserSelectedAnswerIndex();
            
            if (selectedIndex == -1 && !isAutoSkip) {
                Toast.makeText(this, "Please select an answer to continue.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Score calculation: If auto-skipped, treat as wrong/skipped (0 marks usually, but negative in hard mode)
            if (selectedIndex != -1) {
                if (selectedIndex == currentQ.getCorrectAnswerIndex()) {
                    sessionScore += 1;
                } else {
                    if ("Hard".equalsIgnoreCase(difficulty)) sessionScore -= 1;
                }
            } else if (isAutoSkip) {
                // Penalize for skipping in Hard mode if desired, otherwise +0
                if ("Hard".equalsIgnoreCase(difficulty)) sessionScore -= 1; 
            }
            
            scoreText.setText("Score: " + sessionScore);
        }

        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
            if (!isLearnMode)
                startTimer();
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
        if (isQuizFinished)
            return;
        isQuizFinished = true;

        if (countDownTimer != null)
            countDownTimer.cancel();
        SecurityHelper.enableDnd(this, false);

        if (isLearnMode) {
            Toast.makeText(this, "Mastery Session Complete!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        int score = sessionScore;
        // Logic updated to use real-time sessionScore which handles negative marking.

        prefManager.addStats(score);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        QuizResult result = new QuizResult(category + " (Test)", score, questionList.size(), date);
        new DatabaseHelper(this).addResult(result);

        submitResultToBackend(score, questionList.size(), score, questionList.size() - score,
                (int) ((System.currentTimeMillis() - quizStartTime) / 1000), questionList);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("TOPIC", category);
        intent.putExtra("DIFFICULTY", difficulty);
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
        if (!isQuizFinished && !isLearnMode)
            finishQuiz();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isQuizFinished && !isLearnMode) {
            Toast.makeText(this, "App backgrounded - Quiz submitted.", Toast.LENGTH_SHORT).show();
            finishQuiz();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableFullscreen();
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
