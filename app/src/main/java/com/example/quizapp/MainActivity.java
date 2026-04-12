package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.ui.profile.ProfileActivity;
import com.example.quizapp.ui.quiz.QuizActivity;
import com.example.quizapp.ui.topic.CategoryAdapter;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private BottomNavigationView bottomNavigation;
    private SharedPreferencesManager prefManager;
    private TextView userNameText, subtitleText;
    private boolean isLearnModeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new SharedPreferencesManager(this);
        userNameText = findViewById(R.id.userNameText);
        subtitleText = findViewById(R.id.welcomeText);
        
        userNameText.setText("Ready, " + prefManager.getUserName() + "?");

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Handle navigation from Profile
        int targetTab = getIntent().getIntExtra("TARGET_TAB", R.id.nav_test);
        bottomNavigation.setSelectedItemId(targetTab);
        isLearnModeActive = (targetTab == R.id.nav_learn);
        setupCategories(isLearnModeActive);

        setupNavigation();
    }

    private void setupCategories(boolean isLearnMode) {
        this.isLearnModeActive = isLearnMode;
        subtitleText.setText(isLearnMode ? "Master your skills - All Questions" : "Challenge yourself - 20 Questions");
        
        List<String> categories = Arrays.asList(
            "Java", "C++", "Python", "JS", "Git", "OS", "React", "Node.js", "DBMS", "Networks", "C"
        );

        CategoryAdapter adapter = new CategoryAdapter(categories, topic -> {
            if (isLearnMode) {
                startQuiz(topic, "Random", true);
            } else {
                showDifficultyDialog(topic);
            }
        });

        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryRecyclerView.setAdapter(adapter);
    }

    private void showDifficultyDialog(String topic) {
        String[] difficulties = {"Easy", "Medium", "Hard", "Mix (Random)"};
        new AlertDialog.Builder(this)
                .setTitle("Select Difficulty for " + topic)
                .setItems(difficulties, (dialog, which) -> {
                    String diff = (which == 3) ? "Random" : difficulties[which];
                    startQuiz(topic, diff, false);
                })
                .show();
    }

    private void startQuiz(String topic, String diff, boolean learnMode) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("CATEGORY", topic);
        intent.putExtra("DIFFICULTY", diff);
        intent.putExtra("LEARN_MODE", learnMode);
        startActivity(intent);
    }

    private void setupNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_test) {
                setupCategories(false);
                return true;
            } else if (id == R.id.nav_learn) {
                setupCategories(true);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return false;
            }
            return false;
        });
    }
}
