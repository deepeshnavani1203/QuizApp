package com.example.quizapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.QuizResult;
import com.example.quizapp.ui.auth.LoginActivity;
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private TextView profileName, profileEmail, totalQuizzesText, avgScoreText;
    private Button logoutBtn, clearHistoryBtn;
    private BottomNavigationView bottomNavigation;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefManager = new SharedPreferencesManager(this);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        totalQuizzesText = findViewById(R.id.totalQuizzesText);
        avgScoreText = findViewById(R.id.avgScoreText);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        logoutBtn = findViewById(R.id.logoutBtn);
        clearHistoryBtn = findViewById(R.id.clearHistoryBtn);

        profileName.setText(prefManager.getUserName());
        profileEmail.setText(prefManager.getUserEmail());

        String userId = prefManager.getUserId();
        if (userId != null) {
            new Thread(() -> {
                try {
                    JSONObject userProfile = BackendService.getUserProfile(userId);
                    if (userProfile != null) {
                        String name = userProfile.optString("name", prefManager.getUserName());
                        String email = userProfile.optString("email", prefManager.getUserEmail());
                        prefManager.saveBackendUser(userId, name, email);
                        runOnUiThread(() -> {
                            profileName.setText(name);
                            profileEmail.setText(email);
                        });
                    }
                } catch (Exception e) {}
            }).start();
        }

        setupHistory();
        setupNavigation();

        logoutBtn.setOnClickListener(v -> {
            prefManager.setLoggedIn(false);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        clearHistoryBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("This will permanently delete all your quiz history from the server. This cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> clearHistory())
                .setNegativeButton("Cancel", null)
                .show();
        });

    }

    private void setupHistory() {
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(new HistoryAdapter(new ArrayList<>()));

        String userId = prefManager.getUserId();
        if (userId == null) {
            loadLocalHistory();
            return;
        }

        new Thread(() -> {
            List<QuizResult> results = new ArrayList<>();
            int total = 0;
            double avg = 0;
            boolean backendSuccess = false;
            try {
                JSONArray history = BackendService.getUserHistory(userId);
                if (history != null) {
                    backendSuccess = true;
                    double sumPercent = 0;
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject item = history.getJSONObject(i);
                        JSONObject quizObj = item.optJSONObject("quizId");
                        String quizName = (quizObj != null && quizObj.has("title"))
                                ? quizObj.optString("title", "Quiz") : "Quiz";
                        int score = item.optInt("score", 0);
                        int totalQuestions = item.optInt("totalQuestions", 0);
                        String date = item.optString("date", "");
                        results.add(new QuizResult(quizName, score, totalQuestions, date));
                        if (totalQuestions > 0) {
                            sumPercent += (double) score / totalQuestions * 100;
                        }
                    }
                    total = results.size();
                    if (total > 0) avg = sumPercent / total;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final boolean usedBackend = backendSuccess;
            int finalTotal = total;
            double finalAvg = avg;
            runOnUiThread(() -> {
                if (usedBackend) {
                    // Backend responded — show its data (even if empty)
                    historyRecyclerView.setAdapter(new HistoryAdapter(results));
                    totalQuizzesText.setText(String.valueOf(finalTotal));
                    avgScoreText.setText(finalTotal > 0
                            ? String.format(Locale.getDefault(), "%.1f%%", finalAvg) : "0%");
                } else {
                    // Backend unreachable — fall back to local
                    loadLocalHistory();
                }
            });
        }).start();
    }

    private void loadLocalHistory() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<QuizResult> results = dbHelper.getAllResults();
        historyRecyclerView.setAdapter(new HistoryAdapter(results));

        int total = prefManager.getTotalQuizzes();
        totalQuizzesText.setText(String.valueOf(total));
        if (total > 0) {
            double avg = Math.max(0.0, (double) prefManager.getTotalScore() / total);
            avgScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", avg));
        } else {
            avgScoreText.setText("0%");
        }
    }



    private void clearHistory() {
        String userId = prefManager.getUserId();
        clearHistoryBtn.setEnabled(false);

        new Thread(() -> {
            boolean backendCleared = false;
            if (userId != null) {
                backendCleared = BackendService.clearUserHistory(userId);
            }
            // Always clear local DB and stats
            new DatabaseHelper(this).clearHistory();
            prefManager.clearStatsOnly();

            final boolean success = backendCleared || userId == null;
            runOnUiThread(() -> {
                clearHistoryBtn.setEnabled(true);
                if (success) {
                    // Directly reset UI — don't re-fetch (backend already cleared)
                    historyRecyclerView.setAdapter(new HistoryAdapter(new ArrayList<>()));
                    totalQuizzesText.setText("0");
                    avgScoreText.setText("0%");
                    Toast.makeText(this, "History cleared successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Could not reach server. Local history cleared.", Toast.LENGTH_LONG).show();
                    historyRecyclerView.setAdapter(new HistoryAdapter(new ArrayList<>()));
                    totalQuizzesText.setText("0");
                    avgScoreText.setText("0%");
                }
            });
        }).start();
    }

    private void setupNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_test || id == R.id.nav_learn) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("TARGET_TAB", id);
                startActivity(intent);
                return true;
            }
            return id == R.id.nav_profile;
        });
    }
}
