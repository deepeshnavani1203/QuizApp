package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.ui.profile.ProfileActivity;
import com.example.quizapp.ui.quiz.QuizActivity;
import com.example.quizapp.ui.topic.CategoryAdapter;
import com.example.quizapp.utils.CalendarHelper;
import com.example.quizapp.utils.NotificationHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private BottomNavigationView bottomNavigation;
    private SharedPreferencesManager prefManager;
    private TextView userNameText, subtitleText;
    private AppCompatImageButton notifBtn, calendarBtn;
    private boolean isLearnModeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new SharedPreferencesManager(this);

        // Create notification channel and schedule daily reminder at 8 PM
        NotificationHelper.createChannel(this);
        NotificationHelper.scheduleDailyReminder(this, 20);

        userNameText = findViewById(R.id.userNameText);
        subtitleText = findViewById(R.id.welcomeText);

        userNameText.setText("Ready, " + prefManager.getUserName() + "?");

        String userId = prefManager.getUserId();
        if (userId != null) {
            new Thread(() -> {
                try {
                    org.json.JSONObject userProfile = com.example.quizapp.utils.BackendService.getUserProfile(userId);
                    if (userProfile != null) {
                        String name = userProfile.optString("name", prefManager.getUserName());
                        String email = userProfile.optString("email", prefManager.getUserEmail());
                        prefManager.saveBackendUser(userId, name, email);
                        runOnUiThread(() -> {
                            userNameText.setText("Ready, " + name + "?");
                        });
                    }
                } catch (Exception e) {}
            }).start();
        }

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        notifBtn = findViewById(R.id.notifBtn);
        calendarBtn = findViewById(R.id.calendarBtn);

        // Notification bell — show status and let user toggle daily reminder
        notifBtn.setOnClickListener(v -> {
            String[] options = {"Enable Daily Reminder (8 PM)", "Disable Daily Reminder"};
            new AlertDialog.Builder(this)
                .setTitle("🔔 Notifications")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        NotificationHelper.scheduleDailyReminder(this, 20);
                        Toast.makeText(this, "Daily reminder set for 8:00 PM ✓", Toast.LENGTH_SHORT).show();
                    } else {
                        NotificationHelper.cancelDailyReminder(this);
                        Toast.makeText(this, "Daily reminder cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        });

        // Calendar button — add a general study session for today
        calendarBtn.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 10);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            CalendarHelper.addPracticeReminder(this, "Quiz Practice", 0, 0, cal.getTimeInMillis());
        });

        // Handle navigation from Profile
        int targetTab = getIntent().getIntExtra("TARGET_TAB", R.id.nav_test);
        bottomNavigation.setSelectedItemId(targetTab);
        isLearnModeActive = (targetTab == R.id.nav_learn);
        setupCategories(isLearnModeActive);

        setupNavigation();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // 1. DND Permission (Notification Policy Access)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!com.example.quizapp.utils.SecurityHelper.hasDndPermission(this)) {
                showPermissionDialog("DND Access", 
                    "Quizify needs DND access to prevent interruptions during quizzes. Please enable it in the next screen.",
                    () -> com.example.quizapp.utils.SecurityHelper.requestDndPermission(this));
            }
        }

        // 2. Notification Permission (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, 
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, 
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void showPermissionDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Grant", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Later", null)
                .show();
    }

    private void setupCategories(boolean isLearnMode) {
        this.isLearnModeActive = isLearnMode;
        subtitleText.setText(isLearnMode ? "Master your skills - All Questions" : "Challenge yourself - 20 Questions");

        List<String> categories = Arrays.asList(
                "Java",
                "C",
                "C++",
                "Python",
                "JS",
                "Git",
                "OS",
                "React",
                "Node.js",
                "DBMS");

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
        String[] difficulties = { "Easy", "Medium", "Hard", "Mix (Random)" };
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
