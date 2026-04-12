package com.example.quizapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private RecyclerView historyRecyclerView;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        SharedPreferencesManager prefManager = new SharedPreferencesManager(this);
        profileName.setText(prefManager.getUserName());
        profileEmail.setText(prefManager.getUserEmail());

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        HistoryAdapter adapter = new HistoryAdapter(dbHelper.getAllResults());
        historyRecyclerView.setAdapter(adapter);

        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
            return id == R.id.nav_profile;
        });
    }
}
