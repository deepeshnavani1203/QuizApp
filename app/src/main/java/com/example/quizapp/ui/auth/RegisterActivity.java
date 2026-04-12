package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.ui.auth.LoginActivity;
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.SharedPreferencesManager;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button registerBtn;
    private TextView loginLink, errorTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);
        errorTextView = findViewById(R.id.errorTextView);
        progressBar = findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(v -> {
            errorTextView.setVisibility(android.view.View.GONE);
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(android.view.View.VISIBLE);
            registerBtn.setEnabled(false);

            SharedPreferencesManager prefManager = new SharedPreferencesManager(this);
            new Thread(() -> {
                try {
                    JSONObject response = BackendService.signup(name, email, password);
                    if (response != null && response.has("user")) {
                        JSONObject user = response.getJSONObject("user");
                        String userId = user.optString("id");
                        prefManager.saveBackendUser(userId, name, email);
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            registerBtn.setEnabled(true);
                            Toast.makeText(this, "Registration successful. Please login.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        });
                    } else {
                        Log.e("AuthError", "Signup failed: Server response is null or missing user.");
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            registerBtn.setEnabled(true);
                            errorTextView.setText("Registration failed or user already exists.");
                            errorTextView.setVisibility(android.view.View.VISIBLE);
                        });
                    }
                } catch (Exception e) {
                    Log.e("AuthError", "Exception during signup process.", e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        registerBtn.setEnabled(true);
                        errorTextView.setText("Unable to connect. Error: " + e.getMessage());
                        errorTextView.setVisibility(android.view.View.VISIBLE);
                    });
                }
            }).start();
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
