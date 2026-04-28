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
    private static final String KEY_DARK_MODE = "isDarkMode";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void saveUser(String name, String email, String password) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .commit();
    }

    public void saveBackendUser(String userId, String name, String email) {
        sharedPreferences.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .commit();
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
        sharedPreferences.edit()
            .putInt(KEY_TOTAL_SCORE, getTotalScore() + score)
            .putInt(KEY_TOTAL_QUIZZES, getTotalQuizzes() + 1)
            .apply();
    }

    public int getTotalScore() {
        return sharedPreferences.getInt(KEY_TOTAL_SCORE, 0);
    }

    public int getTotalQuizzes() {
        return sharedPreferences.getInt(KEY_TOTAL_QUIZZES, 0);
    }

    public void clearStatsOnly() {
        sharedPreferences.edit()
            .putInt(KEY_TOTAL_SCORE, 0)
            .putInt(KEY_TOTAL_QUIZZES, 0)
            .commit();
    }

    public void clearData() {
        sharedPreferences.edit().clear().commit();
    }

    public void setDarkMode(boolean isDark) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
}
