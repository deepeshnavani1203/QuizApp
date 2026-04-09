package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.startQuizBtn).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.quizapp.ui.quiz.QuizActivity.class);
            startActivity(intent);
        });
    }
}