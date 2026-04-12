package com.example.quizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOPIC_PREFIX = "topic_attempted_";
    private static final String KEY_SCORE_PREFIX = "topic_score_";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUser(String name, String email, String password) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "user@example.com");
    }

    public boolean validateLogin(String email, String password) {
        String savedEmail = pref.getString(KEY_USER_EMAIL, "");
        String savedPassword = pref.getString(KEY_USER_PASSWORD, "");
        return email.equals(savedEmail) && password.equals(savedPassword);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setTopicAttempted(String topic, int score) {
        editor.putBoolean(KEY_TOPIC_PREFIX + topic, true);
        editor.putInt(KEY_SCORE_PREFIX + topic, score);
        editor.apply();
    }

    public boolean isTopicAttempted(String topic) {
        return pref.getBoolean(KEY_TOPIC_PREFIX + topic, false);
    }

    public int getTopicScore(String topic) {
        return pref.getInt(KEY_SCORE_PREFIX + topic, 0);
    }

    public void clearData() {
        editor.clear();
        editor.apply();
    }
}
