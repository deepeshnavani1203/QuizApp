package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.ui.profile.ProfileActivity;
import com.example.quizapp.ui.quiz.QuizActivity;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private RadioGroup categoryGroup, modeGroup;
    private Button startQuizBtn;
    private BottomNavigationView bottomNavigation;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new SharedPreferencesManager(this);
        categoryGroup = findViewById(R.id.categoryGroup);
        modeGroup = findViewById(R.id.modeGroup);
        startQuizBtn = findViewById(R.id.startQuizBtn);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        startQuizBtn.setOnClickListener(v -> {
            String category = getSelectedCategory();
            boolean isLearnMode = modeGroup.getCheckedRadioButtonId() == R.id.modeLearn;
            String difficulty = getSelectedDifficulty();

            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("CATEGORY", category);
            intent.putExtra("LEARN_MODE", isLearnMode);
            intent.putExtra("DIFFICULTY", difficulty);
            startActivity(intent);
        });

        bottomNavigation.setSelectedItemId(R.id.nav_quiz);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return id == R.id.nav_quiz;
        });
    }

    private String getSelectedCategory() {
        int id = categoryGroup.getCheckedRadioButtonId();
        if (id == R.id.catJava) return "Java";
        if (id == R.id.catDBMS) return "DBMS";
        if (id == R.id.catOS) return "OS";
        return "Java";
    }

    private String getSelectedDifficulty() {
        // Since all diff buttons are in a GridLayout, I'll check manually
        if (findViewById(R.id.diffEasy).isClickable() && ((android.widget.RadioButton)findViewById(R.id.diffEasy)).isChecked()) return "Easy";
        if (findViewById(R.id.diffMedium).isClickable() && ((android.widget.RadioButton)findViewById(R.id.diffMedium)).isChecked()) return "Medium";
        if (findViewById(R.id.diffHard).isClickable() && ((android.widget.RadioButton)findViewById(R.id.diffHard)).isChecked()) return "Hard";
        return "Random";
    }
}
