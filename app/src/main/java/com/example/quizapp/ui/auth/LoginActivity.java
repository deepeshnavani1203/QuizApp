package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerLink, errorTextView;
    private ProgressBar progressBar;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new SharedPreferencesManager(this);

        // Redirect if already logged in
        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        errorTextView = findViewById(R.id.errorTextView);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(v -> {
            errorTextView.setVisibility(android.view.View.GONE);
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(android.view.View.VISIBLE);
            loginBtn.setEnabled(false);

            new Thread(() -> {
                try {
                    JSONObject response = BackendService.login(email, password);
                    if (response != null && response.has("user")) {
                        JSONObject user = response.getJSONObject("user");
                        String userId = user.optString("id");
                        String name = user.optString("name");
                        String userEmail = user.optString("email");
                        prefManager.saveBackendUser(userId, name, userEmail);
                        prefManager.setLoggedIn(true);
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            loginBtn.setEnabled(true);
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
                    } else {
                        Log.e("AuthError", "Login failed: Server response is null or invalid user object.");
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            loginBtn.setEnabled(true);
                            errorTextView.setText("Invalid credentials or server unavailable.");
                            errorTextView.setVisibility(android.view.View.VISIBLE);
                        });
                    }
                } catch (Exception e) {
                    Log.e("AuthError", "Exception during login process.", e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        loginBtn.setEnabled(true);
                        errorTextView.setText("Unable to connect. Error: " + e.getMessage());
                        errorTextView.setVisibility(android.view.View.VISIBLE);
                    });
                }
            }).start();
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
