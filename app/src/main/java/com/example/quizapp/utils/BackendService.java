package com.example.quizapp.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendService {
    private static final String TAG = "BackendService";
    public static final String BASE_URL = "http://172.22.19.34:5000";

    private static String doGet(String endpoint) throws IOException {
        Log.d(TAG, "GET --> " + BASE_URL + endpoint);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "GET <-- " + responseCode + " " + endpoint);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                String errorBody = readStream(connection.getErrorStream());
                Log.e(TAG, "GET FAILED " + endpoint + " | code=" + responseCode + " | body=" + errorBody);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            String result = builder.toString();
            Log.d(TAG, "GET SUCCESS " + endpoint + " | length=" + result.length());
            return result;
        } finally {
            if (reader != null) reader.close();
            if (connection != null) connection.disconnect();
        }
    }

    private static String doPost(String endpoint, JSONObject payload) throws IOException {
        Log.d(TAG, "POST --> " + BASE_URL + endpoint + " | payload=" + payload.toString());
        HttpURLConnection connection = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(payload.toString());
            writer.flush();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "POST <-- " + responseCode + " " + endpoint);

            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                String errorBody = readStream(connection.getErrorStream());
                Log.e(TAG, "POST FAILED " + endpoint + " | code=" + responseCode + " | body=" + errorBody);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            String result = builder.toString();
            Log.d(TAG, "POST SUCCESS " + endpoint + " | response=" + result);
            return result;
        } finally {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (connection != null) connection.disconnect();
        }
    }

    private static String doDelete(String endpoint) throws IOException {
        Log.d(TAG, "DELETE --> " + BASE_URL + endpoint);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "DELETE <-- " + responseCode + " " + endpoint);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                String errorBody = readStream(connection.getErrorStream());
                Log.e(TAG, "DELETE FAILED " + endpoint + " | code=" + responseCode + " | body=" + errorBody);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            String result = builder.toString();
            Log.d(TAG, "DELETE SUCCESS " + endpoint + " | response=" + result);
            return result;
        } finally {
            if (reader != null) reader.close();
            if (connection != null) connection.disconnect();
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) builder.append(line);
        reader.close();
        return builder.toString();
    }

    // ─── Auth ────────────────────────────────────────────────────────────────

    public static JSONObject login(String email, String password) throws Exception {
        Log.i(TAG, "login() called for email=" + email);
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        String response = doPost("/api/users/login", payload);
        if (response == null) {
            Log.e(TAG, "login() failed — server returned null response");
            throw new Exception("Server rejected login. Check credentials.");
        }
        Log.i(TAG, "login() success");
        return new JSONObject(response);
    }

    public static JSONObject signup(String name, String email, String password) throws Exception {
        Log.i(TAG, "signup() called for email=" + email);
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("email", email);
        payload.put("password", password);
        String response = doPost("/api/users/signup", payload);
        if (response == null) {
            Log.e(TAG, "signup() failed — server returned null response");
            throw new Exception("Registration rejected. User already exists or server issue.");
        }
        Log.i(TAG, "signup() success");
        return new JSONObject(response);
    }

    // ─── User ────────────────────────────────────────────────────────────────

    public static JSONObject getUserProfile(String userId) {
        Log.i(TAG, "getUserProfile() userId=" + userId);
        try {
            String response = doGet("/api/users/" + userId);
            if (response == null) {
                Log.e(TAG, "getUserProfile() returned null for userId=" + userId);
                return null;
            }
            Log.i(TAG, "getUserProfile() success");
            return new JSONObject(response);
        } catch (Exception e) {
            Log.e(TAG, "getUserProfile() exception: " + e.getMessage(), e);
            return null;
        }
    }

    public static JSONArray getUserHistory(String userId) {
        Log.i(TAG, "getUserHistory() userId=" + userId);
        try {
            String response = doGet("/api/users/" + userId + "/history");
            if (response == null) {
                Log.e(TAG, "getUserHistory() returned null for userId=" + userId);
                return null;
            }
            Log.i(TAG, "getUserHistory() success");
            return new JSONArray(response);
        } catch (Exception e) {
            Log.e(TAG, "getUserHistory() exception: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean clearUserHistory(String userId) {
        Log.i(TAG, "clearUserHistory() userId=" + userId);
        try {
            String response = doDelete("/api/users/" + userId + "/history");
            if (response == null) {
                Log.e(TAG, "clearUserHistory() returned null — delete may have failed");
                return false;
            }
            Log.i(TAG, "clearUserHistory() success");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "clearUserHistory() exception: " + e.getMessage(), e);
            return false;
        }
    }

    public static JSONObject getUserAnalytics(String userId) {
        Log.i(TAG, "getUserAnalytics() userId=" + userId);
        try {
            String response = doGet("/api/users/" + userId + "/analytics");
            if (response == null) {
                Log.e(TAG, "getUserAnalytics() returned null for userId=" + userId);
                return null;
            }
            Log.i(TAG, "getUserAnalytics() success");
            return new JSONObject(response);
        } catch (Exception e) {
            Log.e(TAG, "getUserAnalytics() exception: " + e.getMessage(), e);
            return null;
        }
    }

    // ─── Quiz ────────────────────────────────────────────────────────────────

    public static JSONArray getQuizList() {
        Log.i(TAG, "getQuizList() called");
        try {
            String response = doGet("/api/quizzes");
            if (response == null) {
                Log.e(TAG, "getQuizList() returned null");
                return null;
            }
            Log.i(TAG, "getQuizList() success");
            return new JSONArray(response);
        } catch (Exception e) {
            Log.e(TAG, "getQuizList() exception: " + e.getMessage(), e);
            return null;
        }
    }

    public static JSONObject getQuizById(String quizId) {
        Log.i(TAG, "getQuizById() quizId=" + quizId);
        try {
            String response = doGet("/api/quizzes/" + quizId);
            if (response == null) {
                Log.e(TAG, "getQuizById() returned null for quizId=" + quizId);
                return null;
            }
            Log.i(TAG, "getQuizById() success");
            return new JSONObject(response);
        } catch (Exception e) {
            Log.e(TAG, "getQuizById() exception: " + e.getMessage(), e);
            return null;
        }
    }

    // ─── Result ──────────────────────────────────────────────────────────────

    public static JSONObject submitResult(JSONObject payload) {
        Log.i(TAG, "submitResult() called | quizId=" + payload.optString("quizId")
                + " userId=" + payload.optString("userId")
                + " score=" + payload.optString("score"));
        try {
            String response = doPost("/api/results/submit", payload);
            if (response == null) {
                Log.e(TAG, "submitResult() returned null — result not saved on server");
                return null;
            }
            Log.i(TAG, "submitResult() success");
            return new JSONObject(response);
        } catch (Exception e) {
            Log.e(TAG, "submitResult() exception: " + e.getMessage(), e);
            return null;
        }
    }
}
