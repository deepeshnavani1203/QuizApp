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
import com.example.quizapp.utils.DatabaseHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private TextView profileName, profileEmail, totalQuizzesText, avgScoreText;
    private Button logoutBtn, resetDataBtn;
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
        resetDataBtn = findViewById(R.id.resetDataBtn);

        profileName.setText(prefManager.getUserName());
        profileEmail.setText(prefManager.getUserEmail());

        setupHistory();
        setupNavigation();

        logoutBtn.setOnClickListener(v -> {
            prefManager.setLoggedIn(false);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        resetDataBtn.setOnClickListener(v -> showResetDialog());
    }

    private void setupHistory() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<QuizResult> results = dbHelper.getAllResults();
        
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter(results);
        historyRecyclerView.setAdapter(adapter);

        // Calculate Cumulative Stats from SP as requested
        int total = prefManager.getTotalQuizzes();
        totalQuizzesText.setText(String.valueOf(total));

        if (total > 0) {
            double avg = (double) prefManager.getTotalScore() / total;
            avgScoreText.setText(String.format(Locale.getDefault(), "%.1f pts", avg));
        } else {
            avgScoreText.setText("0 pts");
        }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Data?")
                .setMessage("This will clear your history and stats. Action cannot be undone.")
                .setPositiveButton("RESET", (dialog, which) -> {
                    prefManager.clearData();
                    new DatabaseHelper(this).clearHistory();
                    Toast.makeText(this, "Data Reset Successful", Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .setNegativeButton("CANCEL", null)
                .show();
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
