package com.example.quizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "QuizifyPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOTAL_SCORE = "totalScore";
    private static final String KEY_TOTAL_QUIZZES = "totalQuizzes";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void saveUser(String name, String email, String password) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public void saveBackendUser(String userId, String name, String email) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public boolean validateLogin(String email, String password) {
        String savedEmail = sharedPreferences.getString(KEY_USER_EMAIL, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        return email.equals(savedEmail) && password.equals(savedPassword);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Guest");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "guest@example.com");
    }

    public void addStats(int score) {
        editor.putInt(KEY_TOTAL_SCORE, getTotalScore() + score);
        editor.putInt(KEY_TOTAL_QUIZZES, getTotalQuizzes() + 1);
        editor.apply();
    }

    public int getTotalScore() {
        return sharedPreferences.getInt(KEY_TOTAL_SCORE, 0);
    }

    public int getTotalQuizzes() {
        return sharedPreferences.getInt(KEY_TOTAL_QUIZZES, 0);
    }

    public void clearData() {
        editor.clear();
        editor.apply();
    }
}
