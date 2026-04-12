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
    public static final String BASE_URL = "http://10.0.2.2:5000";

    private static String doGet(String endpoint) throws IOException {
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
            if (responseCode != HttpURLConnection.HTTP_OK) {
                String errorBody = readStream(connection.getErrorStream());
                Log.e(TAG, "GET " + endpoint + " failed: " + responseCode + " " + errorBody);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            if (reader != null)
                reader.close();
            if (connection != null)
                connection.disconnect();
        }
    }

    private static String doPost(String endpoint, JSONObject payload) throws IOException {
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
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                String errorBody = readStream(connection.getErrorStream());
                Log.e(TAG, "POST " + endpoint + " failed: " + responseCode + " " + errorBody);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            if (writer != null)
                writer.close();
            if (reader != null)
                reader.close();
            if (connection != null)
                connection.disconnect();
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null)
            return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    public static JSONObject login(String email, String password) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("email", email);
            payload.put("password", password);
            String response = doPost("/api/users/login", payload);
            return response == null ? null : new JSONObject(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONObject signup(String name, String email, String password) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("name", name);
            payload.put("email", email);
            payload.put("password", password);
            String response = doPost("/api/users/signup", payload);
            return response == null ? null : new JSONObject(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONArray getQuizList() {
        try {
            String response = doGet("/api/quizzes");
            return response == null ? null : new JSONArray(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONObject getQuizById(String quizId) {
        try {
            String response = doGet("/api/quizzes/" + quizId);
            return response == null ? null : new JSONObject(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONObject submitResult(JSONObject payload) {
        try {
            String response = doPost("/api/results/submit", payload);
            return response == null ? null : new JSONObject(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONArray getUserHistory(String userId) {
        try {
            String response = doGet("/api/users/" + userId + "/history");
            return response == null ? null : new JSONArray(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static JSONObject getUserAnalytics(String userId) {
        try {
            String response = doGet("/api/users/" + userId + "/analytics");
            return response == null ? null : new JSONObject(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}