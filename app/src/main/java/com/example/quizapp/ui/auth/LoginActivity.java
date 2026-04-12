package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
    private TextView registerLink;
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

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

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
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast
                                .makeText(this, "Invalid credentials or server error", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Unable to login. Check your network.", Toast.LENGTH_SHORT)
                            .show());
                }
            }).start();
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
